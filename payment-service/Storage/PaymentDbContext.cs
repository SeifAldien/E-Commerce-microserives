using Microsoft.EntityFrameworkCore;

namespace PaymentService.Storage;

public class PaymentDbContext : DbContext
{
    public PaymentDbContext(DbContextOptions<PaymentDbContext> options) : base(options) {}

    public DbSet<Payment> Payments => Set<Payment>();
}

public class Payment
{
    public long PaymentId { get; set; }
    public long OrderId { get; set; }
    public long UserId { get; set; }
    public string Status { get; set; } = "PENDING";
    public DateTime PaymentDate { get; set; } = DateTime.UtcNow;
    public double Amount { get; set; }
}


