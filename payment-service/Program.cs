using Microsoft.EntityFrameworkCore;
using PaymentService.Storage;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddDbContext<PaymentDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("SqlServer") ??
                         "Server=localhost,1433;Database=paymentdb;User Id=sa;Password=Your_password123;Encrypt=False;TrustServerCertificate=True;"));

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

// Ensure database created
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<PaymentDbContext>();
    db.Database.EnsureCreated();
}

app.MapPost("/payments", async (HttpRequest req, PaymentDbContext db, PaymentRequest pr) =>
{
    var userIdHeader = req.Headers["X-User-Id"].FirstOrDefault();
    if (string.IsNullOrWhiteSpace(userIdHeader)) return Results.Unauthorized();
    if (pr.amount <= 0) return Results.BadRequest(new { message = "Invalid amount" });

    var payment = new Payment
    {
        OrderId = pr.orderId,
        UserId = long.Parse(userIdHeader),
        Amount = pr.amount,
        Status = "CONFIRMED",
        PaymentDate = DateTime.UtcNow
    };
    db.Payments.Add(payment);
    await db.SaveChangesAsync();
    return Results.Ok(payment);
});

app.MapGet("/payments/{orderId:long}", async (long orderId, HttpRequest req, PaymentDbContext db) =>
{
    var userIdHeader = req.Headers["X-User-Id"].FirstOrDefault();
    if (string.IsNullOrWhiteSpace(userIdHeader)) return Results.Unauthorized();
    var uid = long.Parse(userIdHeader);
    var payment = await db.Payments.FirstOrDefaultAsync(p => p.OrderId == orderId && p.UserId == uid);
    return payment is null ? Results.NotFound() : Results.Ok(payment);
});

app.Run();

public record PaymentRequest(long orderId, double amount);


