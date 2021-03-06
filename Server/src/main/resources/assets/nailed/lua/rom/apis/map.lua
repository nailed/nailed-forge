
local native = (map.native and map.native()) or map
local map = {}

local function wrap(_sFunction)
	return function(...)
		return native[_sFunction](...)
	end
end

map.countdown = function(seconds, message)
    local ellapsed = 0
    repeat
        local out, num = string.gsub(message, "%%s", textutils.secondsToShortTimeString(seconds - ellapsed))
        map.sendTimeUpdate("" .. out)
        sleep(1)
        ellapsed = ellapsed + 1
    until ellapsed >= seconds
end

map.countup = function(message)
    local ellapsed = 0
    while true do
        local out, num = string.gsub(message, "%%s", textutils.formatSeconds(ellapsed))
        map.sendTimeUpdate("" .. out)
        sleep(1)
        ellapsed = ellapsed + 1
    end
end

map.native = function()
    return native
end

for k,v in pairs(native) do
	if type(k) == "string" and type(v) == "function" then
		if map[k] == nil then
			map[k] = wrap(k)
		end
	end
end

local env = getfenv()
for k,v in pairs(map) do
	env[k] = v
end
