local args = {...}
if #args < 1 then
	print("Usage: mkdir <path>")
	return
end
fs.makeDir(shell.resolve(args[1]))
