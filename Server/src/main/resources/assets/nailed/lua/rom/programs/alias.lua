local args = {...}
if #args > 2 then
	print("Usage: alias <alias> <program>")
	return
end

local alias = args[1]
local program = args[2]

if alias and program then
	shell.setAlias(alias, program)
elseif alias then
	shell.clearAlias(alias)
else
	local aliases = shell.aliases()
	local list = {}
	for alias, sCommand in pairs(aliases) do
		table.insert(list, alias)
	end
	table.sort(list)
	textutils.pagedTabulate(list)
end
	