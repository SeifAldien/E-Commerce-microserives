using CartService.Services;
using StackExchange.Redis;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var redisConnection = builder.Configuration.GetConnectionString("Redis") ?? "localhost:6379";
builder.Services.AddSingleton<IConnectionMultiplexer>(_ => ConnectionMultiplexer.Connect(redisConnection));
builder.Services.AddSingleton<CartRepository>();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.MapGet("/cart", async (HttpRequest req, CartRepository repo) =>
{
    var userId = req.Headers["X-User-Id"].FirstOrDefault();
    if (string.IsNullOrWhiteSpace(userId)) return Results.Unauthorized();
    var items = await repo.GetCartItems(userId);
    return Results.Ok(items);
});

app.MapPost("/cart", async (HttpRequest req, CartRepository repo, CartItem item) =>
{
    var userId = req.Headers["X-User-Id"].FirstOrDefault();
    if (string.IsNullOrWhiteSpace(userId)) return Results.Unauthorized();
    await repo.AddOrUpdateItem(userId, item);
    return Results.Ok();
});

app.MapPost("/cart/hold", async (HttpRequest req, CartRepository repo) =>
{
    var userId = req.Headers["X-User-Id"].FirstOrDefault();
    if (string.IsNullOrWhiteSpace(userId)) return Results.Unauthorized();
    await repo.ExtendTtl(userId, TimeSpan.FromDays(7));
    return Results.Ok();
});

app.MapPost("/cart/confirm", async (HttpRequest req, CartRepository repo) =>
{
    var userId = req.Headers["X-User-Id"].FirstOrDefault();
    if (string.IsNullOrWhiteSpace(userId)) return Results.Unauthorized();
    await repo.RemoveTtl(userId);
    return Results.Ok();
});

app.Run();

public record CartItem(string cartItemId, long productId, int quantity);


