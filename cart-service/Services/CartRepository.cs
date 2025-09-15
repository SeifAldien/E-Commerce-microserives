using StackExchange.Redis;
using System.Text.Json;

namespace CartService.Services;

public class CartRepository
{
    private readonly IConnectionMultiplexer _redis;
    private static readonly TimeSpan DefaultTtl = TimeSpan.FromHours(24);

    public CartRepository(IConnectionMultiplexer redis)
    {
        _redis = redis;
    }

    private string Key(string userId) => $"cart:{userId}";

    public async Task<List<CartItem>> GetCartItems(string userId)
    {
        var db = _redis.GetDatabase();
        var key = Key(userId);
        var entries = await db.HashGetAllAsync(key);
        var list = new List<CartItem>();
        foreach (var entry in entries)
        {
            var item = JsonSerializer.Deserialize<CartItem>(entry.Value!);
            if (item != null) list.Add(item);
        }
        return list;
    }

    public async Task AddOrUpdateItem(string userId, CartItem item)
    {
        var db = _redis.GetDatabase();
        var key = Key(userId);
        await db.HashSetAsync(key, item.cartItemId, JsonSerializer.Serialize(item));
        await db.KeyExpireAsync(key, DefaultTtl);
    }

    public async Task ExtendTtl(string userId, TimeSpan ttl)
    {
        var db = _redis.GetDatabase();
        await db.KeyExpireAsync(Key(userId), ttl);
    }

    public async Task RemoveTtl(string userId)
    {
        var db = _redis.GetDatabase();
        await db.KeyPersistAsync(Key(userId));
    }
}


