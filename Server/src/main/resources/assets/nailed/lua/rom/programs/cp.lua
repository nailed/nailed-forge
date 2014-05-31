local args = {...}
if #args < 2 then
	print("Usage: cp <source> <destination>")
	return
end

local source = shell.resolve(args[1])
local dest = shell.resolve(args[2])
if fs.exists(dest) and fs.isDir(dest) then
	dest = fs.combine(dest, fs.getName(source))
end
fs.copy(source, dest)
