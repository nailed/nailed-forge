local args = {...}
if #args < 1 then
	print("Usage: cd <path>")
	return
end

local newDir = shell.resolve(args[1])
if fs.isDir(newDir) then
	shell.setDir(newDir)
else
  	printError("Not a directory")
  	return
end
