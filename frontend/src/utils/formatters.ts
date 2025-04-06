export function formatCurrency(amount: number | undefined, currency: string = '$'): string {
    if (amount === undefined) return '-'
    return currency === '₹'
        ? `₹${amount.toLocaleString('en-IN')}`
        : `$${amount.toLocaleString('en-US')}`
}

export function formatDate(date: Date): string {
    return new Intl.DateTimeFormat('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    }).format(date);
}

export function formatPercent(value: number): string {
    return new Intl.NumberFormat('en-US', {
        style: 'percent',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(value);
}

export function formatEnum(value: string): string {
    // Convert SNAKE_CASE or UPPERCASE to Title Case
    return value
        .toLowerCase()
        .split('_')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ');
}
