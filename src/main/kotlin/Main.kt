package chess

import kotlin.math.abs

fun main(args: Array<String>) {
    println("Pawns-Only Chess")
    println("First Player's name:")
    val pl1 = readLine()!!
    println("Second Player's name:")
    val pl2 = readLine()!!
    Game.initNames(pl1, pl2)
    Game.printBoard()
    var move = ""
    var message: String? = null
    do {
        Game.printOfferToMove()
        move = readLine()!!
        if (Game.makeMove(move)) {
            message = Game.checkState()
            if (message != null)
                move = "exit"
        }
    } while (move != "exit")
    println("${message ?: ""}Bye!")
}

object Game {
    val board = MutableList(8) { Array(8) { " " } }
    var player1 = ""
    var player2 = ""
    var ch = true
    var passant = false
    var wpawns = 8
    var bpawns = 8
    init {
        board[1] = Array(8) { "W" }
        board[6] = Array(8) { "B" }
    }

    fun initNames(a: String, b: String) {
        player1 = a
        player2 = b
    }

    fun printBoard() {
        println("  +---+---+---+---+---+---+---+---+")
        println("8 | ${board[7].joinToString(" | ")} |")
        println("  +---+---+---+---+---+---+---+---+")
        println("7 | ${board[6].joinToString(" | ")} |")
        println("  +---+---+---+---+---+---+---+---+")
        println("6 | ${board[5].joinToString(" | ")} |")
        println("  +---+---+---+---+---+---+---+---+")
        println("5 | ${board[4].joinToString(" | ")} |")
        println("  +---+---+---+---+---+---+---+---+")
        println("4 | ${board[3].joinToString(" | ")} |")
        println("  +---+---+---+---+---+---+---+---+")
        println("3 | ${board[2].joinToString(" | ")} |")
        println("  +---+---+---+---+---+---+---+---+")
        println("2 | ${board[1].joinToString(" | ")} |")
        println("  +---+---+---+---+---+---+---+---+")
        println("1 | ${board[0].joinToString(" | ")} |")
        println("  +---+---+---+---+---+---+---+---+")
        println("    a   b   c   d   e   f   g   h")
    }

    fun checkMove(s: String): Array<Boolean> = when {
        !s.matches("[a-h][1-8][a-h][1-8]|^exit".toRegex()) -> { println("Invalid Input"); arrayOf(false, false) }
        s == "exit" -> arrayOf(false, false)
        ch && board["${s[1]}".toInt() - 1][s[0].code - 'a'.code] != "W" -> { println("No white pawn at ${s[0]}${s[1]}"); arrayOf(false, false) }
        !ch && board["${s[1]}".toInt() - 1][s[0].code - 'a'.code] != "B" -> { println("No black pawn at ${s[0]}${s[1]}"); arrayOf(false, false) }
        ch && (s[3] - s[1] != 1 && s[1] != '2' || s[3] - s[1] != 1 && s[3] - s[1] != 2 && s[1] == '2') -> { println("Invalid Input"); arrayOf(false, false) }
        !ch && (s[1] - s[3] != 1 && s[1] != '7' || s[1] - s[3] != 1 && s[1] - s[3] != 2 && s[1] == '7') -> { println("Invalid Input"); arrayOf(false, false) }
        ch && abs(s[0].code - s[2].code) == 1 && s[3] - s[1] == 1 && board["${s[3]}".toInt() - 1][s[2].code - 'a'.code] == "B" -> { bpawns--; arrayOf(true, false) }
        !ch && abs(s[0].code - s[2].code) == 1 && s[1] - s[3] == 1 && board["${s[3]}".toInt() - 1][s[2].code - 'a'.code] == "W" -> { wpawns--; arrayOf(true, false) }
        ch && passant && abs(s[0].code - s[2].code) == 1 && s[3] - s[1] == 1 && board["${s[3]}".toInt() - 2][s[2].code - 'a'.code] == "B" -> { bpawns--; arrayOf(true, true) }
        !ch && passant && abs(s[0].code - s[2].code) == 1 && s[1] - s[3] == 1 && board["${s[3]}".toInt()][s[2].code - 'a'.code] == "W" -> { wpawns--; arrayOf(true, true) }
        s[0] != s[2] -> { println("Invalid Input"); arrayOf(false, false) }
        board["${s[3]}".toInt() - 1][s[2].code - 'a'.code] != " " -> { println("Invalid Input"); arrayOf(false, false) }
        else -> arrayOf(true, true) //arrayOf(false, true)
    }

    fun printOfferToMove() = println("${if (ch) Game.player1 else Game.player2}'s turn:")

    fun checkPassant(j: Int, i: Int) = when {
        ch && j >= 1 && j <= 6 -> board[i - 1][j - 1] == "B" || board[i - 1][j + 1] == "B"
        ch && j == 0 -> board[i - 1][j + 1] == "B"
        ch && j == 7 -> board[i - 1][j - 1] == "B"
        !ch && j >= 1 && j <= 6 -> board[i - 1][j - 1] == "W" || board[i - 1][j + 1] == "W"
        !ch && j == 0 -> board[i - 1][j + 1] == "W"
        !ch && j == 7 -> board[i - 1][j - 1] == "W"
        else -> false
    }

    fun makeMove(s: String): Boolean {
        val check = checkMove(s)
        if (check[0]) {
            board["${s[1]}".toInt() - 1][s[0].code - 'a'.code] = " "
            board["${s[3]}".toInt() - 1][s[2].code - 'a'.code] = if (ch) "W" else "B"
            if (check[1])
                board[if (ch) "${s[3]}".toInt() - 2 else "${s[3]}".toInt()][s[2].code - 'a'.code] = " "
            passant = checkPassant(s[2].code - 'a'.code, "${s[3]}".toInt())
            ch = !ch
            printBoard()
            return true
        }
        return false
    }

    fun checkState() = when {
        wpawns == 0 || board[0].contains("B") -> "Black wins!\n"
        bpawns == 0 || board[7].contains("W") -> "White wins!\n"
        !movable() -> "Stalemate!\n"
        else -> null
    }

    fun movable(): Boolean {
        for (i in 1..6)
            for (j in 0..7)
                if (board[i][j] != " ")
                    when (ch) {
                        true -> if (board[i][j] == "W" && (passant || board[i + 1][j] == " " ||
                                    j == 0 && board[i + 1][j + 1] == "B" ||
                                    j == 7 && board[i + 1][j - 1] == "B" ||
                                    j in 1..6 && (board[i + 1][j + 1] == "B" || board[i + 1][j - 1] == "B")))
                            return true
                        false -> if (board[i][j] == "B" && (passant || board[i - 1][j] == " " ||
                                    j == 0 && board[i - 1][j + 1] == "W" ||
                                    j == 7 && board[i - 1][j - 1] == "W" ||
                                    j in 1..6 && (board[i - 1][j + 1] == "W" || board[i - 1][j - 1] == "W")))
                            return true
                    }
        return false
    }
}