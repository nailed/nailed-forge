local args = {...}
if #args > 0 then
    local task = shell.launch(unpack(args))
    if task then
        shell.switch(task)
    end
else
    local task = shell.switch(shell.launch("shell"))
    if task then
        shell.switch(task)
    end
end
