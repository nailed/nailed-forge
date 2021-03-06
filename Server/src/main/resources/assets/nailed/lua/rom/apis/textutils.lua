
function slowWrite( sText, nRate )
	nRate = nRate or 20
	if nRate < 0 then
		error( "rate must be positive" )
	end
	local nSleep = 1 / nRate
		
	sText = tostring( sText )
	local x,y = term.getCursorPos(x,y)
	local len = string.len( sText )
	
	for n=1,len do
		term.setCursorPos( x, y )
		sleep( nSleep )
		local nLines = write( string.sub( sText, 1, n ) )
		local newX, newY = term.getCursorPos()
		y = newY - nLines
	end
end

function slowPrint( sText, nRate )
	slowWrite( sText, nRate)
	print()
end

function formatTime( nTime, bTwentyFourHour )
	local sTOD = nil
	if not bTwentyFourHour then
		if nTime >= 12 then
			sTOD = "PM"
		else
			sTOD = "AM"
		end
		if nTime >= 13 then
			nTime = nTime - 12
		end
	end

	local nHour = math.floor(nTime)
	local nMinute = math.floor((nTime - nHour)*60)
	if sTOD then
		return string.format( "%d:%02d %s", nHour, nMinute, sTOD )
	else
		return string.format( "%d:%02d", nHour, nMinute )
	end
end

local function makePagedScroll( _term, _nFreeLines )
	local nativeScroll = _term.scroll
	local nFreeLines = _nFreeLines or 0
	return function( _n )
		for n=1,_n do
			nativeScroll( 1 )
			
			if nFreeLines <= 0 then
				local w,h = _term.getSize()
				_term.setCursorPos( 1, h )
				_term.write( "Press any key to continue" )
				os.pullEvent( "key" )
				_term.clearLine()
				_term.setCursorPos( 1, h )
			else
				nFreeLines = nFreeLines - 1
			end
		end
    end
end

function pagedPrint( _sText, _nFreeLines )
	local nativeScroll = term.scroll
	term.scroll = makePagedScroll( term, _nFreeLines )
	local result
	local ok, err = pcall( function()
		result = print( _sText )
	end )
	term.scroll = nativeScroll
	if not ok then
		error( err )
	end
	return result
end

local function tabulateCommon( bPaged, ... )
	local tAll = { ... }
	
	local w,h = term.getSize()
	local nMaxLen = w / 8
	for n, t in ipairs( tAll ) do
		if type(t) == "table" then
			for n, sItem in pairs(t) do
				nMaxLen = math.max( string.len( sItem ) + 1, nMaxLen )
			end
		end
	end
	local nCols = math.floor( w / nMaxLen )

	local nLines = 0
	local function newLine()
		if bPaged and nLines >= (h-3) then
			pagedPrint()
		else
			print()
		end
		nLines = nLines + 1
	end
	
	local function drawCols( _t )
		local nCol = 1
		for n, s in ipairs( _t ) do
			if nCol > nCols then
				nCol = 1
				newLine()
			end

			local cx,cy = term.getCursorPos()
			cx = 1 + (nCol - 1) * (w / nCols)
			term.setCursorPos( cx, cy )
			term.write( s )

			nCol = nCol + 1  	
		end
		print()
	end
	for n, t in ipairs( tAll ) do
		if type(t) == "table" then
			if #t > 0 then
				drawCols( t )
			end
		elseif type(t) == "number" then
			term.setTextColor( t )
		end
	end	
end

function tabulate( ... )
	tabulateCommon( false, ... )
end

function pagedTabulate( ... )
	tabulateCommon( true, ... )
end

local function serializeImpl( t, tTracking )	
	local sType = type(t)
	if sType == "table" then
		if tTracking[t] ~= nil then
			error( "Cannot serialize table with recursive entries" )
		end
		tTracking[t] = true
		
		local result = "{"
		for k,v in pairs(t) do
			result = result..("["..serializeImpl(k, tTracking).."]="..serializeImpl(v, tTracking)..",")
		end
		result = result.."}"
		return result
		
	elseif sType == "string" then
		return string.format( "%q", t )
	
	elseif sType == "number" or sType == "boolean" or sType == "nil" then
		return tostring(t)
		
	else
		error( "Cannot serialize type "..sType )
		
	end
end

function serialize( t )
	local tTracking = {}
	return serializeImpl( t, tTracking )
end

function unserialize( s )
	local func, e = loadstring( "return "..s, "serialize" )
	if not func then
		return s
	else
		setfenv( func, {} )
		return func()
	end
end

function urlEncode( str )
	if str then
		str = string.gsub(str, "\n", "\r\n")
		str = string.gsub(str, "([^%w ])",
		function (c)
			return string.format("%%%02X", string.byte(c))
		end)
		str = string.gsub (str, " ", "+")
	end
	return str	
end

function secondsToShortTimeString(secs)
    local hours = math.floor(secs / 3600)
    local minutes = math.floor((secs % 3600) / 60)
    local seconds = math.floor(secs % 60)
    local ret = ""
    local append = false
    if hours ~= 0 or append then
        ret = ret .. hours
        ret = ret .. ":"
        append = true
    end
    if minutes ~= 0 or append then
        ret = ret .. minutes
        ret = ret .. ":"
        append = true
    end
    if seconds ~= 0 or append then
        if seconds < 10 and append then
            ret = ret .. "0"
        end
        ret = ret .. seconds
        if not append then
            ret = ret .. " Second"
            if seconds ~= 1 then ret = ret .. "s" end
        end
    end
    return ret
end

function formatSeconds(secs)
    local hours = math.floor(secs / 3600)
    local minutes = math.floor((secs % 3600) / 60)
    local seconds = math.floor(secs % 60)
    local ret = ""
    local append = false
    if hours ~= 0 or append then
        ret = ret .. hours
        ret = ret .. ":"
        append = true
    end
    if minutes ~= 0 or append then
        if minutes < 10 and append then
            ret = ret .. "0"
        end
        ret = ret .. minutes
        ret = ret .. ":"
        append = true
    end
    if seconds ~= 0 or append then
        if seconds < 10 and append then
            ret = ret .. "0"
        end
        ret = ret .. seconds
    end
    return ret
end
