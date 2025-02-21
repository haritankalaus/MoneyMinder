package com.jay.accountmanager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jay.accountmanager.dto.BillDTO;
import com.jay.accountmanager.dto.BillStatsDTO;
import com.jay.accountmanager.model.Bill;
import com.jay.accountmanager.repository.BillRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillService {
    
	private final BillRepository billRepository;
    
    private final PersonService personService;

    @Transactional(readOnly = true)
    public List<BillDTO> getAllBills() {
        return billRepository.findByPersonId(personService.getCurrentPerson().getId())
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BillDTO getBill(Long id) {
    	Long currentPersonId = personService.getCurrentPerson().getId();
        return billRepository.findById(id)
            .filter(bill -> bill.getPerson().getId().equals(currentPersonId))
            .map(this::toDTO)
            .orElseThrow(() -> new RuntimeException("Bill not found"));
    }

    @Transactional
    public BillDTO createBill(BillDTO dto) {
        Bill bill = toEntity(dto);
        bill.setPerson(personService.getCurrentPerson());
        return toDTO(billRepository.save(bill));
    }

    @Transactional
    public BillDTO updateBill(Long id, BillDTO dto) {
    	Long currentPersonId = personService.getCurrentPerson().getId();
        Bill bill = billRepository.findById(id)
            .filter(e -> e.getPerson().getId().equals(currentPersonId))
            .orElseThrow(() -> new RuntimeException("Bill not found"));
        
        updateEntityFromDTO(bill, dto);
        return toDTO(billRepository.save(bill));
    }

    @Transactional
    public void deleteBill(Long id) {
    	Long currentPersonId = personService.getCurrentPerson().getId();
        billRepository.findById(id)
            .filter(bill -> bill.getPerson().getId().equals(currentPersonId))
            .ifPresent(billRepository::delete);
    }

    @Transactional(readOnly = true)
    public BillStatsDTO getBillStats() {
    	Long currentPersonId = personService.getCurrentPerson().getId();

        LocalDate today = LocalDate.now();
        LocalDate nextMonth = today.plusMonths(1);
        
        BillStatsDTO stats = new BillStatsDTO();
        
        // Calculate total bills
        BigDecimal totalBills = billRepository.findByPersonId(currentPersonId)
            .stream()
            .map(Bill::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalBills(totalBills);
        
        // Get monthly bills
        BigDecimal monthlyBills = billRepository.getMonthlyBills(currentPersonId);
        stats.setMonthlyBills(monthlyBills != null ? monthlyBills : BigDecimal.ZERO);
        
        // Calculate due soon (next 7 days)
        List<Bill> dueSoonBills = billRepository.findByPersonIdAndDueDateBetween(
        		currentPersonId, today, today.plusDays(7));
        BigDecimal dueSoon = dueSoonBills.stream()
            .map(Bill::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setDueSoon(dueSoon);
        
        // Get upcoming payments
        List<BillStatsDTO.UpcomingPaymentDTO> upcomingPayments = billRepository
            .findByPersonIdAndDueDateBetween(currentPersonId, today, nextMonth)
            .stream()
            .map(bill -> {
                BillStatsDTO.UpcomingPaymentDTO payment = new BillStatsDTO.UpcomingPaymentDTO();
                payment.setAmount(bill.getAmount());
                payment.setDueDate(bill.getDueDate().toString());
                return payment;
            })
            .collect(Collectors.toList());
        stats.setUpcomingPayments(upcomingPayments);
        
        // Calculate required by next paydate (assuming monthly pay cycle)
        //@TODO
        LocalDate nextPaydate = null;
        if(today.getDayOfMonth() <= 15)
        	nextPaydate = today.withDayOfMonth(15);
        else
        	nextPaydate = today.withDayOfMonth(1);
        if (nextPaydate.isBefore(today)) {
            nextPaydate = nextPaydate.plusMonths(1);
        }
        BigDecimal requiredByNextPayday = billRepository
            .findByPersonIdAndDueDateBetween(currentPersonId, today, nextPaydate)
            .stream()
            .map(Bill::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setRequiredByNextPayday(requiredByNextPayday);
        
        return stats;
    }

    private BillDTO toDTO(Bill bill) {
        BillDTO dto = new BillDTO();
        dto.setId(bill.getId());
        dto.setName(bill.getName());
        dto.setAmount(bill.getAmount());
        dto.setType(bill.getType());
        dto.setDescription(bill.getDescription());
        dto.setDueDate(bill.getDueDate());
        dto.setRecurrenceType(bill.getRecurrenceType());
        dto.setDayOfWeek(bill.getDayOfWeek());
        dto.setDayOfMonth(bill.getDayOfMonth());
        dto.setMonthAndDay(bill.getMonthAndDay());
        dto.setCustomDays(bill.getCustomDays());
        dto.setRecurrenceEnd(bill.getRecurrenceEnd());
        dto.setAutoPay(bill.isAutoPay());
        dto.setPaymentMethod(bill.getPaymentMethod());
        dto.setCategory(bill.getCategory());
        if (bill.getAccount() != null) {
            dto.setAccountId(bill.getAccount().getId());
        }
        return dto;
    }

    private Bill toEntity(BillDTO dto) {
        Bill bill = new Bill();
        updateEntityFromDTO(bill, dto);
        return bill;
    }

    private void updateEntityFromDTO(Bill bill, BillDTO dto) {
        bill.setName(dto.getName());
        bill.setAmount(dto.getAmount());
        bill.setType(dto.getType());
        bill.setDescription(dto.getDescription());
        bill.setDueDate(dto.getDueDate());
        bill.setRecurrenceType(dto.getRecurrenceType());
        bill.setDayOfWeek(dto.getDayOfWeek());
        bill.setDayOfMonth(dto.getDayOfMonth());
        bill.setMonthAndDay(dto.getMonthAndDay());
        bill.setCustomDays(dto.getCustomDays());
        bill.setRecurrenceEnd(dto.getRecurrenceEnd());
        bill.setAutoPay(dto.isAutoPay());
        bill.setPaymentMethod(dto.getPaymentMethod());
        bill.setCategory(dto.getCategory());
    }
}
