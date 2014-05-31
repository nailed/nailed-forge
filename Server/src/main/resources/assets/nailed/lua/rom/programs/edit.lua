-- Get file to edit
local args = {...}
if #args == 0 then
	print("Usage: edit <path>")
	return
end

-- Error checking
local sPath = shell.resolve(args[1])
local bReadOnly = fs.isReadOnly(sPath)
if fs.exists(sPath) and fs.isDir(sPath) then
	printError("Cannot edit a directory.")
	return
end

local x,y = 1,1
local w,h = term.getSize()
local scrollX, scrollY = 0,0

local tLines = {}
local bRunning = true

-- Colors
local highlightColor, keywordColor, commentColor, textColor, bgColor
bgColor = colors.black
textColor = colors.textColorNormal
highlightColor = colors.textColorHighlite
keywordColor = colors.textColorHighlite
commentColor = colors.green
stringColor = colors.red

-- Menus
local bMenu = false
local nMenuItem = 1

local tMenuItems
if bReadOnly then
	tMenuItems = {"Exit"}
else
	tMenuItems = {"Save", "Exit"}
end
	
local sStatus = "Press Ctrl to access menu"

local function load(_sPath)
	tLines = {}
	if fs.exists(_sPath) then
		local file = io.open(_sPath, "r")
		local sLine = file:read()
		while sLine do
			table.insert(tLines, sLine)
			sLine = file:read()
		end
		file:close()
	end
	
	if #tLines == 0 then
		table.insert(tLines, "")
	end
end

local function save(_sPath)
	-- Create intervening folder
	local sDir = sPath:sub(1, sPath:len() - fs.getName(sPath):len())
	if not fs.exists(sDir) then
		fs.makeDir(sDir)
	end

	-- Save
	local file = nil
	local function innerSave()
		file = fs.open(_sPath, "w")
		if file then
			for n, sLine in ipairs(tLines) do
				file.write(sLine .. "\n")
			end
		else
			error("Failed to open ".._sPath)
		end
	end
	
	local ok = pcall(innerSave)
	if file then 
		file.close()
	end
	return ok
end

local tKeywords = {
	["and"] = true,
	["break"] = true,
	["do"] = true,
	["else"] = true,
	["elseif"] = true,
	["end"] = true,
	["false"] = true,
	["for"] = true,
	["function"] = true,
	["if"] = true,
	["in"] = true,
	["local"] = true,
	["nil"] = true,
	["not"] = true,
	["or"] = true,
	["repeat"] = true,
	["return"] = true,
	["then"] = true,
	["true"] = true,
	["until"]= true,
	["while"] = true,
}

local function tryWrite(sLine, regex, color)
	local match = string.match(sLine, regex)
	if match then
		if type(color) == "number" then
			term.setTextColor(color)
		else
			term.setTextColor(color(match))
		end
		term.write(match)
		term.setTextColor(textColor)
		return string.sub(sLine, string.len(match) + 1)
	end
	return nil
end

local function writeHighlighted(sLine)
	while string.len(sLine) > 0 do	
		sLine = 
			tryWrite(sLine, "^%-%-%[%[.-%]%]", commentColor) or
			tryWrite(sLine, "^%-%-.*", commentColor) or
			tryWrite(sLine, "^\".-[^\\]\"", stringColor) or
			tryWrite(sLine, "^\'.-[^\\]\'", stringColor) or
			tryWrite(sLine, "^%[%[.-%]%]", stringColor) or
			tryWrite(sLine, "^[%w_]+", function(match)
				if tKeywords[ match ] then
					return keywordColor
				end
				return textColor
			end) or
			tryWrite(sLine, "^[^%w_]", textColor)
	end
end

local function redrawText()
	for y=1,h-1 do
		term.setCursorPos(1 - scrollX, y)
		term.clearLine()

		local sLine = tLines[ y + scrollY ]
		if sLine ~= nil then
			writeHighlighted(sLine)
		end
	end
	term.setCursorPos(x - scrollX, y - scrollY)
end

local function redrawLine(_nY)
	local sLine = tLines[_nY]
	term.setCursorPos(1 - scrollX, _nY - scrollY)
	term.clearLine()
	writeHighlighted(sLine)
	term.setCursorPos(x - scrollX, _nY - scrollY)
end

local function redrawMenu()
    term.setCursorPos(1, h)
	term.clearLine()

	local sLeft, sRight
	local nLeftColor, nLeftHighlight1, nLeftHighlight2
	if bMenu then
		local sMenu = ""
		for n,sItem in ipairs(tMenuItems) do
			if n == nMenuItem then
				nLeftHighlight1 = sMenu:len() + 1
				nLeftHighlight2 = sMenu:len() + sItem:len() + 2
			end
			sMenu = sMenu.." "..sItem.." "
		end
		sLeft = sMenu
		nLeftColor = textColor
	else
		sLeft = sStatus
		nLeftColor = highlightColor
	end
	
	-- Left goes last so that it can overwrite the line numbers.
	sRight = "Ln "..y
	term.setTextColor(highlightColor)
	term.setCursorPos(w-sRight:len() + 1, h)
	term.write(sRight)

	sRight = tostring(y)
	term.setTextColor(textColor)
	term.setCursorPos(w-sRight:len() + 1, h)
	term.write(sRight)

	if sLeft then
		term.setCursorPos(1, h)
		term.setTextColor(nLeftColor)
		term.write(sLeft)		
		if nLeftHighlight1 then
			term.setTextColor(highlightColor)
			term.setCursorPos(nLeftHighlight1, h)
			term.write("[")
			term.setCursorPos(nLeftHighlight2, h)
			term.write("]")
		end
		term.setTextColor(textColor)
	end
	
	-- Cursor highlights selection
	term.setCursorPos(x - scrollX, y - scrollY)
end

local tMenuFuncs = {
	Save=function()
		if bReadOnly then
			sStatus = "Access denied"
		else
			local ok, err = save(sPath)
			if ok then
				sStatus="Saved to "..sPath
			else
				sStatus="Error saving to "..sPath
			end
		end
		redrawMenu()
	end,
	Exit=function()
		bRunning = false
	end
}

local function doMenuItem(_n)
	tMenuFuncs[tMenuItems[_n]]()
	if bMenu then
		bMenu = false
		term.setCursorBlink(true)
	end
	redrawMenu()
end

local function setCursor(x, y)
	local screenX = x - scrollX
	local screenY = y - scrollY
	
	local bRedraw = false
	if screenX < 1 then
		scrollX = x - 1
		screenX = 1
		bRedraw = true
	elseif screenX > w then
		scrollX = x - w
		screenX = w
		bRedraw = true
	end
	
	if screenY < 1 then
		scrollY = y - 1
		screenY = 1
		bRedraw = true
	elseif screenY > h-1 then
		scrollY = y - (h-1)
		screenY = h-1
		bRedraw = true
	end
	
	if bRedraw then
		redrawText()
	end
	term.setCursorPos(screenX, screenY)
	
	-- Statusbar now pertains to menu, it would probably be safe to redraw the menu on every key event.
	redrawMenu()
end

-- Actual program functionality begins
load(sPath)

term.setBackgroundColor(bgColor)
term.clear()
term.setCursorPos(x,y)
term.setCursorBlink(true)

redrawText()
redrawMenu()

-- Handle input
while bRunning do
	local sEvent, param, param2, param3 = os.pullEvent()
	if sEvent == "key" then
		if param == keys.up then
			-- Up
			if not bMenu then
				if y > 1 then
					-- Move cursor up
					y = y - 1
					x = math.min(x, string.len(tLines[y]) + 1)
					setCursor(x, y)
				end
			end
		elseif param == keys.down then
			-- Down
			if not bMenu then
				-- Move cursor down
				if y < #tLines then
					y = y + 1
					x = math.min(x, string.len(tLines[y]) + 1)
					setCursor(x, y)
				end
			end
		elseif param == keys.tab then
			-- Tab
			if not bMenu and not bReadOnly then
				-- Indent line
				tLines[y]="  "..tLines[y]
				x = x + 2
				setCursor(x, y)
				redrawLine(y)
			end
		elseif param == keys.pageUp then
			-- Page Up
			if not bMenu then
				-- Move up a page
				local sx,sy=term.getSize()
				y=y-sy-1
				if y<1 then	y=1 end
				x = math.min(x, string.len(tLines[y]) + 1)
				setCursor(x, y)
			end
		elseif param == keys.pageDown then
			-- Page Down
			if not bMenu then
				-- Move down a page
				local sx,sy=term.getSize()
				if y<#tLines-sy-1 then
					y = y+sy-1
				else
					y = #tLines
				end
				x = math.min(x, string.len(tLines[y]) + 1)
				setCursor(x, y)
			end
		elseif param == keys.home then
			-- Home
			if not bMenu then
				-- Move cursor to the beginning
				x=1
				setCursor(x,y)
			end
		elseif param == keys["end"] then
			-- End
			if not bMenu then
				-- Move cursor to the end
				x = string.len(tLines[y]) + 1
				setCursor(x,y)
			end
		elseif param == keys.left then
			-- Left
			if not bMenu then
				if x > 1 then
					-- Move cursor left
					x = x - 1
				elseif x==1 and y>1 then
					x = string.len(tLines[y-1]) + 1
					y = y - 1
				end
				setCursor(x, y)
			else
				-- Move menu left
				nMenuItem = nMenuItem - 1
				if nMenuItem < 1 then
					nMenuItem = #tMenuItems
				end
				redrawMenu()
			end
		elseif param == keys.right then
			-- Right
			if not bMenu then
				if x < string.len(tLines[y]) + 1 then
					-- Move cursor right
					x = x + 1
				elseif x==string.len(tLines[y]) + 1 and y<#tLines then
					x = 1
					y = y + 1
				end
				setCursor(x, y)
			else
				-- Move menu right
				nMenuItem = nMenuItem + 1
				if nMenuItem > #tMenuItems then
					nMenuItem = 1
				end
				redrawMenu()
			end
		elseif param == keys.delete then
			-- Delete
			if not bMenu and not bReadOnly then
				if  x < string.len(tLines[y]) + 1 then
					local sLine = tLines[y]
					tLines[y] = string.sub(sLine,1,x-1) .. string.sub(sLine,x+1)
					redrawLine(y)
				elseif y<#tLines then
					tLines[y] = tLines[y] .. tLines[y+1]
					table.remove(tLines, y+1)
					redrawText()
					redrawMenu()
				end
			end
		elseif param == keys.backspace then
			-- Backspace
			if not bMenu and not bReadOnly then
				if x > 1 then
					-- Remove character
					local sLine = tLines[y]
					tLines[y] = string.sub(sLine,1,x-2) .. string.sub(sLine,x)
					redrawLine(y)
			
					x = x - 1
					setCursor(x, y)
				elseif y > 1 then
					-- Remove newline
					local sPrevLen = string.len(tLines[y-1])
					tLines[y-1] = tLines[y-1] .. tLines[y]
					table.remove(tLines, y)
					redrawText()
				
					x = sPrevLen + 1
					y = y - 1
					setCursor(x, y)
				end
			end
		elseif param == keys.enter then
			-- Enter
			if not bMenu and not bReadOnly then
				-- Newline
				local sLine = tLines[y]
				local _,spaces=string.find(sLine,"^[ ]+")
				if not spaces then
					spaces=0
				end
				tLines[y] = string.sub(sLine,1,x-1)
				table.insert(tLines, y+1, string.rep(' ',spaces)..string.sub(sLine,x))
				redrawText()
			
				x = spaces+1
				y = y + 1
				setCursor(x, y)
			elseif bMenu then
				-- Menu selection
				doMenuItem(nMenuItem)
			end
		elseif param == keys.leftCtrl or param == keys.rightCtrl then
			-- Menu toggle
			bMenu = not bMenu
			if bMenu then
				term.setCursorBlink(false)
				nMenuItem = 1
			else
				term.setCursorBlink(true)
			end
			redrawMenu()
		end
		
	elseif sEvent == "char" then
		if not bMenu and not bReadOnly then
			-- Input text
			local sLine = tLines[y]
			tLines[y] = string.sub(sLine,1,x-1) .. param .. string.sub(sLine,x)
			redrawLine(y)
		
			x = x + string.len(param)
			setCursor(x, y)
		elseif bMenu then
			-- Select menu items
			for n,sMenuItem in ipairs(tMenuItems) do
				if string.lower(string.sub(sMenuItem,1,1)) == string.lower(param) then
					doMenuItem(n)
					break
				end
			end
		end
		
	elseif sEvent == "mouse_click" then
		if not bMenu then
			if param == 1 then
				-- Left click
				local cx,cy = param2, param3
				if cy < h then
					y = math.min(math.max(scrollY + cy, 1), #tLines)
					x = math.min(math.max(scrollX + cx, 1), string.len(tLines[y]) + 1)
					setCursor(x, y)
				end
			end
		end
		
	elseif sEvent == "mouse_scroll" then
		if not bMenu then
			if param == -1 then
				-- Scroll up
				if scrollY > 0 then
					-- Move cursor up
					scrollY = scrollY - 1
					redrawText()
				end
			
			elseif param == 1 then
				-- Scroll down
				local nMaxScroll = #tLines - (h-1)
				if scrollY < nMaxScroll then
					-- Move cursor down
					scrollY = scrollY + 1
					redrawText()
				end
				
			end
		end
	end
end

-- Cleanup
term.clear()
term.setCursorBlink(false)
term.setCursorPos(1, 1)