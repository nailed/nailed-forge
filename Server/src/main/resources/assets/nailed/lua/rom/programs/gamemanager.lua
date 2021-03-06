
local gameRunning = false
local gameRoutine

local function drawStartButton()
    local width, height = term.getSize()
    local left = width - 9
    term.setBackgroundColor(colors.blue)
    term.setCursorPos(left, 2)
    term.write("         ")
    term.setCursorPos(left, 3)
    term.write("  Start  ")
    term.setCursorPos(left, 4)
    term.write("         ")
    term.setBackgroundColor(colors.black)
    term.setCursorPos(1,1)
end

local function drawStopButton()
    local width, height = term.getSize()
    local left = width - 9
    term.setBackgroundColor(colors.red)
    term.setCursorPos(left, 2)
    term.write("         ")
    term.setCursorPos(left, 3)
    term.write("  Stop   ")
    term.setCursorPos(left, 4)
    term.write("         ")
    term.setBackgroundColor(colors.black)
    term.setCursorPos(1,1)
end

local function drawSidebar()
    local x, y = term.getCursorPos()
    local width, height = term.getSize()
    local left = width - 11
    term.setBackgroundColor(colors.gray)
    local i = 1
    while i <= height do
        term.setCursorPos(left, i)
        term.setBackgroundColor(colors.gray)
        term.write(" ")
        term.setBackgroundColor(colors.black)
        term.write("           ")
        i = i + 1
    end
    term.setBackgroundColor(colors.black)
    if gameRunning then
        drawStopButton()
    else
        drawStartButton()
    end
    term.setCursorPos(x,y)
end

local function startGame()
    local realPrint = print
    gameRunning = true
    gameRoutine = coroutine.create(function()
        term.setTextColor(colors.lime)
        print("Starting the game")
        term.setTextColor(colors.white)
        map.onStarted()
        os.run({print = function(...)
            realPrint(...)
            drawSidebar()
        end}, "/mappack/gamescript.lua")
        gameRunning = false
        term.setTextColor(colors.lime)
        print("Game Finished!")
        term.setTextColor(colors.white)
        map.onStopped(true)
        drawSidebar()
    end)
    coroutine.resume(gameRoutine)
end

local function stopGame()
    gameRunning = false
    --coroutine.resume(gameRoutine, "timer", -1) --force the coroutine to check it's state by dispatching a fake timer event
    --coroutine.resume(gameRoutine)
    printError("Stopped the game!")
    map.onStopped(false)
    drawSidebar()
end

drawSidebar()
while true do
    local data = {os.pullEventRaw()}
    local event = data[1]
    local p1 = data[2]
    local p2 = data[3]
    local p3 = data[4]
    if gameRoutine ~= nil and gameRunning then
        coroutine.resume(gameRoutine, unpack(data))
    end
    if event == "game_start" then
        startGame()
    elseif event == "game_stop" then
        stopGame()
    elseif event == "mouse_click" and p1 == 1 then
        if p2 >= 42 and p2 <= 50 and p3 >= 2 and p3 <=4 then
            if gameRunning then
                stopGame()
            else
                startGame()
            end
        end
    end
end
