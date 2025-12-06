package com.jht.vault

import com.jht.vault.db.DatabaseInitializer
import java.util.Scanner

fun main() {
    val dbInit = DatabaseInitializer
    dbInit.initialize("src/main/resources/data/vault.db")

    val scanner = Scanner(System.`in`)
    while (true) {
        println("1. Create")
        println("2. Read")
        println("3. Update")
        println("4. Delete")
        println("5. Exit")
        print("Select an option: ")
        when (scanner.nextLine()) { //TODO: implement below
            "1" -> println("Create operation not implemented")
            "2" -> println("Read operation not implemented")
            "3" -> println("Update operation not implemented")
            "4" -> println("Delete operation not implemented")
            "5" -> {
                println("Exiting.")
                break
            }
            else -> println("Invalid option")
        }
    }
}