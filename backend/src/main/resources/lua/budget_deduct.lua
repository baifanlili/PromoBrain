-- KEYS[1] = ad:budget:remaining:{campaignId}
-- KEYS[2] = ad:click:deduct:{requestId}
-- ARGV[1] = cost
-- ARGV[2] = request ttl seconds

if redis.call('exists', KEYS[2]) == 1 then
    return 2
end

local remain = tonumber(redis.call('get', KEYS[1]))
local cost = tonumber(ARGV[1])

if remain == nil then
    return -1
end

if remain < cost then
    return 0
end

redis.call('decrbyfloat', KEYS[1], cost)
redis.call('set', KEYS[2], '1', 'EX', ARGV[2])
return 1

