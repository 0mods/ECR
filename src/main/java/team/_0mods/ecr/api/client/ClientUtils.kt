package team._0mods.ecr.api.client

fun isCursorAtPos(cursorX: Int, cursorY: Int, x: Int, y: Int, width: Int, height: Int) : Boolean =
    cursorX >= x && cursorY >=y && cursorX <= x + width && cursorY <= y + height