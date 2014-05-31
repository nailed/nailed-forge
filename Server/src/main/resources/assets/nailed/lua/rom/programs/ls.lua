local args = {...}

local dir = shell.dir()
if args[1] ~= nil then
	dir = shell.resolve(args[1])
end

-- Sort into dirs/files, and calculate column count
local all = fs.list(dir)
local files = {}
local dirs = {}

for n, item in pairs(all) do
	if string.sub(item, 1, 1) ~= "." then
		local sPath = fs.combine(dir, item)
		if fs.isDir(sPath) then
			table.insert(dirs, item)
		else
			table.insert(files, item)
		end
	end
end
table.sort(dirs)
table.sort(files)

textutils.pagedTabulate(colors.green, dirs, colors.textColorNormal, files)
