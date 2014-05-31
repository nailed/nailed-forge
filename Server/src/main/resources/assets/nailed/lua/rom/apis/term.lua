
local native = (term.native and term.native()) or term
local redirectTarget = native

local function wrap( _sFunction )
	return function( ... )
		return redirectTarget[ _sFunction ]( ... )
	end
end

local term = {}

term.redirect = function( target )
	if target == nil or type( target ) ~= "table" then
		error( "Invalid redirect target", -1 )
	end
	for k,v in pairs( native ) do
		if type( k ) == "string" and type( v ) == "function" then
			if type( target[k] ) ~= "function" then
				target[k] = function()
					error( "Redirect object is missing method "..k.."." )
				end
			end
		end
	end
	local oldRedirectTarget = redirectTarget
	redirectTarget = target
	return oldRedirectTarget
end

term.current = function()
    return redirectTarget
end

term.native = function()
    return native
end

for k,v in pairs( native ) do
	if type( k ) == "string" and type( v ) == "function" then
		if term[k] == nil then
			term[k] = wrap( k )
		end
	end
end

local env = getfenv()
for k,v in pairs( term ) do
	env[k] = v
end
