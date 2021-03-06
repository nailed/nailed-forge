
function create( parent, nX, nY, nWidth, nHeight, bStartVisible )
    -- Setup
    local bVisible = (bStartVisible ~= false)
    local nCursorX = 1
    local nCursorY = 1
    local bCursorBlink = false
    local nTextColor = colors.white
    local nBackgroundColor = colors.black
    local sEmpty = string.rep( " ", nWidth )
    local tLines = {}
    do
        local tEmpty = { { sEmpty, nTextColor, nBackgroundColor } }
        for y=1,nHeight do
            tLines[y] = tEmpty
        end
    end

    -- Helper functions
    local function updateCursorPos()
        if nCursorX >= 1 and nCursorY >= 1 and
           nCursorX <= nWidth and nCursorY <= nHeight then
            parent.setCursorPos( nX + nCursorX - 1, nY + nCursorY - 1 )
        else
            parent.setCursorPos( 0, 0 )
        end
    end
    
    local function updateCursorBlink()
        parent.setCursorBlink( bCursorBlink )
    end
    
    local function updateCursorColor()
        parent.setTextColor( nTextColor )
    end
    
    local function redrawLine( n )
        parent.setCursorPos( nX, nY + n - 1 )
        local tLine = tLines[ n ]
        for m=1,#tLine do
            local tBit = tLine[ m ]
            parent.setTextColor( tBit[2] )
            parent.setBackgroundColor( tBit[3] )
            parent.write( tBit[1] )
        end
    end

    local function lineLen( tLine )
        local nLength = 0
        for n=1,#tLine do
            nLength = nLength + string.len( tLine[n][1] )
        end
        return nLength
    end

    local function lineSub( tLine, nStart, nEnd )
        --assert( math.floor(nStart) == nStart )
        --assert( math.floor(nEnd) == nEnd )
        --assert( nStart >= 1 )
        --assert( nEnd >= nStart )
        --assert( nEnd <= lineLen( tLine ) )
        local tSubLine = {}
        local nBitStart = 1
        for n=1,#tLine do
            local tBit = tLine[n]
            local sBit = tBit[1]
            local nBitEnd = nBitStart + string.len( sBit ) - 1
            if nBitEnd >= nStart and nBitStart <= nEnd then
                if nBitStart >= nStart and nBitEnd <= nEnd then
                    -- Include bit wholesale
                    table.insert( tSubLine, tBit )
                    --assert( lineLen( tSubLine ) == (math.min(nEnd, nBitEnd) - nStart + 1) )
                elseif nBitStart < nStart and nBitEnd <= nEnd then
                    -- Include end of bit
                    table.insert( tSubLine, {
                        string.sub( sBit, nStart - nBitStart + 1 ),
                        tBit[2], tBit[3]
                    } )
                    --assert( lineLen( tSubLine ) == (math.min(nEnd, nBitEnd) - nStart + 1) )
                elseif nBitStart >= nStart and nBitEnd > nEnd then
                    -- Include beginning of bit
                    table.insert( tSubLine, {
                        string.sub( sBit, 1, nEnd - nBitStart + 1 ),
                        tBit[2], tBit[3]
                    } )
                    --assert( lineLen( tSubLine ) == (math.min(nEnd, nBitEnd) - nStart + 1) )
                else
                    -- Include middle of bit
                    table.insert( tSubLine, {
                        string.sub( sBit, nStart - nBitStart + 1, nEnd - nBitStart + 1 ),
                        tBit[2], tBit[3]
                    } )
                    --assert( lineLen( tSubLine ) == (math.min(nEnd, nBitEnd) - nStart + 1) )
                end
            end
            nBitStart = nBitEnd + 1
        end
        --assert( lineLen( tSubLine ) == (nEnd - nStart + 1) )
        return tSubLine
    end

    local function lineJoin( tLine1, tLine2 )
        local tNewLine = {}
        if tLine1[#tLine1][2] == tLine2[1][2] and
           tLine1[#tLine1][3] == tLine2[1][3] then
            -- Merge middle bits
            for n=1,#tLine1-1 do
                table.insert( tNewLine, tLine1[n] )
            end
            table.insert( tNewLine, {
                tLine1[#tLine1][1] .. tLine2[1][1],
                tLine2[1][2], tLine2[1][3]
            } )
            for n=2,#tLine2 do
                table.insert( tNewLine, tLine2[n] )
            end
            --assert( lineLen( tNewLine ) == lineLen(tLine1) + lineLen(tLine2) )
        else
            -- Just concatenate
            for n=1,#tLine1 do
                table.insert( tNewLine, tLine1[n] )
            end
            for n=1,#tLine2 do
                table.insert( tNewLine, tLine2[n] )
            end
            --assert( lineLen( tNewLine ) == lineLen(tLine1) + lineLen(tLine2) )
        end
        return tNewLine
    end

    local function redraw()
        for n=1,nHeight do
            redrawLine( n )
        end
    end

    local window = {}

    -- Terminal implementation
    function window.write( sText )
        if nCursorY >= 1 and nCursorY <= nHeight then
            local nLen = string.len( sText )
            local nStart = nCursorX
            local nEnd = nStart + nLen - 1
            --assert( math.floor(nStart) == nStart )
            --assert( math.floor(nEnd) == nEnd )
            if nStart <= nWidth and nEnd >= 1 then
                -- Construct new line
                local tLine = tLines[ nCursorY ]
                if nStart == 1 and nEnd == nWidth then
                    -- Exactly replace line
                    tLine = {
                        { sText, nTextColor, nBackgroundColor }
                    }
                    --assert( lineLen( tLine ) == nWidth )
                elseif nStart <= 1 and nEnd >= nWidth then
                    -- Overwrite line with subset
                    tLine = {
                        { string.sub( sText, 1 - nStart + 1, nWidth - nStart + 1 ), nTextColor, nBackgroundColor }
                    }
                    --assert( lineLen( tLine ) == nWidth )
                elseif nStart <= 1 then
                    -- Overwrite beginning of line
                    tLine = lineJoin(
                        { { string.sub( sText, 1 - nStart + 1 ), nTextColor, nBackgroundColor } },
                        lineSub( tLine, nEnd + 1, nWidth )
                    )
                    --assert( lineLen( tLine ) == nWidth )
                elseif nEnd >= nWidth then
                    -- Overwrite end of line
                    tLine = lineJoin(
                        lineSub( tLine, 1, nStart - 1 ),
                        { { string.sub( sText, 1, nWidth - nStart + 1 ), nTextColor, nBackgroundColor } }
                    )
                    --assert( lineLen( tLine ) == nWidth )
                else
                    -- Overwrite middle of line
                    tLine = lineJoin(
                        lineJoin(
                            lineSub( tLine, 1, nStart - 1 ),
                            { { sText, nTextColor, nBackgroundColor } }
                        ),
                        lineSub( tLine, nEnd + 1, nWidth )
                    )
                    --assert( lineLen( tLine ) == nWidth )
                end

                -- Store new line
                tLines[ nCursorY ] = tLine
                nCursorX = nEnd + 1

                -- Redraw new line
                if bVisible then
                    redrawLine( nCursorY )
                    updateCursorPos()
                    updateCursorColor()
                end
            end
        end
    end

    function window.clear()
        local tEmpty = { { sEmpty, nTextColor, nBackgroundColor } }
        for y=1,nHeight do
            tLines[y] = tEmpty
        end
        if bVisible then
            redraw()
            updateCursorPos()
            updateCursorColor()
        end
    end

    function window.clearLine()
        if nCursorY >= 1 and nCursorY <= nHeight then
            tLines[ nCursorY ] = { { sEmpty, nTextColor, nBackgroundColor } }
            if bVisible then
                redrawLine( nCursorY )
                updateCursorPos()
                updateCursorColor()
            end
        end
    end

    function window.getCursorPos()
        return nCursorX, nCursorY
    end

    function window.setCursorPos( x, y )
        nCursorX = math.floor( x )
        nCursorY = math.floor( y )
        if bVisible then
            updateCursorPos()
        end
    end

    function window.setCursorBlink( blink )
        bCursorBlink = blink
        if bVisible then
            updateCursorBlink()
        end
    end

    function window.setTextColor( color )
        nTextColor = color
        if bVisible then
            updateCursorColor()
        end
    end

    function window.setBackgroundColor( color )
        nBackgroundColor = color
    end

    function window.getSize()
        return nWidth, nHeight
    end

    function window.scroll( n )
        if n ~= 0 then
            local tNewLines = {}
            local tEmpty = { { sEmpty, nTextColor, nBackgroundColor } }
            for newY=1,nHeight do
                local y = newY + n
                if y >= 1 and y <= nHeight then
                    tNewLines[newY] = tLines[y]
                else
                    tNewLines[newY] = tEmpty
                end
            end
            tLines = tNewLines
            if bVisible then
                redraw()
                updateCursorPos()
                updateCursorColor()
            end
        end
    end

    -- Other functions
    function window.setVisible( bVis )
        if bVisible ~= bVis then
            bVisible = bVis
            if bVisible then
                window.redraw()
            end
        end
    end

    function window.redraw()
        if bVisible then
            redraw()
            updateCursorBlink()
            updateCursorColor()
            updateCursorPos()
        end
    end

    function window.restoreCursor()
        if bVisible then
            updateCursorBlink()
            updateCursorColor()
            updateCursorPos()
        end
    end

    function window.getPosition()
        return nX, nY
    end

    function window.reposition( nNewX, nNewY, nNewWidth, nNewHeight )
        nX = nNewX
        nY = nNewY
        if nNewWidth and nNewHeight then
            sEmpty = string.rep( " ", nNewWidth )
            local tNewLines = {}
            local tEmpty = { { sEmpty, nTextColor, nBackgroundColor } }
            for y=1,nNewHeight do
                if y > nHeight then
                    tNewLines[y] = tEmpty
                else
                    if nNewWidth == nWidth then
                        tNewLines[y] = tLines[y]
                    elseif nNewWidth < nWidth then
                        tNewLines[y] = lineSub( tLines[y], 1, nNewWidth )
                    else
                        tNewLines[y] = lineJoin( tLines[y], { { string.sub( sEmpty, nWidth + 1, nNewWidth ), nTextColor, nBackgroundColor } } )
                    end
                end
            end
            nWidth = nNewWidth
            nHeight = nNewHeight
            tLines = tNewLines
        end
        if bVisible then
            window.redraw()
        end
    end

    if bVisible then
        window.redraw()
    end
    return window
end
