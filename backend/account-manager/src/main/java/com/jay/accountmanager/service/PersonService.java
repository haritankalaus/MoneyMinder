package com.jay.accountmanager.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jay.accountmanager.dto.CreatePersonRequest;
import com.jay.accountmanager.dto.PersonDTO;
import com.jay.accountmanager.model.Person;
import com.jay.accountmanager.repository.PersonRepository;
import com.jay.security.models.User;
import com.jay.security.repository.UserRepository;
import com.jay.security.services.AuthenticationFacade;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository personRepository;
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;

    public PersonDTO getCurrentPersonProfile() {
    	User user = getCurrentUser();
    	if(!personRepository.findById(authenticationFacade.getLoggedinUserId()).isPresent() ) {
    		return mapToDTO(user);
    	} else {
    		Person person = getCurrentPerson();
    		return mapToDTO(person,user);
    	}
    }

    @Transactional
    public PersonDTO updateProfile(PersonDTO personDTO) {
        Person person = getCurrentPerson();
        User user = person.getUser();
        user.setFirstName(personDTO.getFirstName());
        user.setLastName(personDTO.getLastName());
        Person savedPerson = personRepository.save(person);
        userRepository.save(user);
        return mapToDTO(savedPerson,user);
    }

    @Transactional
    public PersonDTO updatePreferences(PersonDTO preferencesDTO) {
        Person person = getCurrentPerson();
        person.setLanguage(preferencesDTO.getLanguage());
        person.setCurrency(preferencesDTO.getCurrency());
        person.setNotifications(preferencesDTO.isNotifications());
        Person savedPerson = personRepository.save(person);
        return mapToDTO(savedPerson,person.getUser());
    }

    @Transactional
    public PersonDTO createPerson(CreatePersonRequest request) {
        // Create new person
        Person person = new Person();
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        person.setUser(user);
        person.setLanguage(request.getLanguage() != null ? request.getLanguage() : "en");
        person.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        person.setNotifications(request.isNotifications());
        
        Person savedPerson = personRepository.save(person);
        return mapToDTO(savedPerson, user);
    }

    public Person getPerson(Long userId) {
    	return personRepository.findByUserId(userId).get();
    }
    
    public Person getCurrentPerson() {
        return personRepository.findById(authenticationFacade.getLoggedinUserId())
                .orElseThrow(() -> new RuntimeException("Person not found"));
    }

    public User getCurrentUser() {
        return authenticationFacade.getCurrentUser();
    }
    
    private PersonDTO mapToDTO(Person person,User user) {
        return PersonDTO.builder()
                .id(person.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getUsername())
                .language(person.getLanguage())
                .currency(person.getCurrency())
                .notifications(person.isNotifications())
                .build();
    }
    

    private PersonDTO mapToDTO(User user) {
        return PersonDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getUsername())
                .build();
    }
}
