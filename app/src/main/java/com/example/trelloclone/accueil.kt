package com.example.trelloclone

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class accueil : AppCompatActivity() {

    private lateinit var boardContainer: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accueil)

        boardContainer = findViewById(R.id.boardContainer)
        sharedPreferences = getSharedPreferences("TrelloClone", Context.MODE_PRIVATE)

        val createBoardButton = findViewById<Button>(R.id.createBoardButton)
        createBoardButton.setOnClickListener {
            val intent = Intent(this, BoardsActivity::class.java)
            startActivity(intent) // Lancement de l'activité
        }

        // Charger les tableaux au démarrage
        loadBoards()
    }

    private fun createNewBoard() {
        val boardName = "Tableau " + (getBoards().size + 1)
        addBoard(boardName)
        Toast.makeText(this, "Tableau '$boardName' créé", Toast.LENGTH_SHORT).show()
    }

    private fun addBoard(boardName: String) {
        val boards = getBoards()
        if (!boards.contains(boardName)) {
            boards.add(boardName)
            saveBoards(boards)
            createBoardView(boardName)
        } else {
            Toast.makeText(this, "Ce tableau existe déjà", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadBoards() {
        val boards = getBoards()
        for (board in boards) {
            createBoardView(board)
        }
    }

    private fun createBoardView(boardName: String) {
        // Conteneur vertical du tableau
        val boardLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(android.R.color.white)
            setPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                marginEnd = 16
            }
            elevation = 8f // Ombre pour l'effet 3D
            id = View.generateViewId() // Attribuer un ID unique pour pouvoir le supprimer plus tard
        }

        // Conteneur horizontal pour le titre et le bouton de suppression
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Titre du tableau
        val boardTitle = TextView(this).apply {
            text = boardName
            textSize = 18f
            setPadding(8, 8, 8, 8)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        // Bouton de suppression
        val deleteButton = Button(this).apply {
            text = "🗑️"
            setOnClickListener {
                deleteBoard(boardName, boardLayout)
            }
        }

        // Ajout du titre et du bouton de suppression au header
        headerLayout.addView(boardTitle)
        headerLayout.addView(deleteButton)

        // Bouton pour ajouter un item
        val addItemButton = Button(this).apply {
            text = "Ajouter une carte"
            setOnClickListener {
                addItemToBoard(boardLayout, boardName)
            }
        }

        // Conteneur pour les items (cartes)
        val itemsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }

        // Charger les cartes du tableau
        loadItemsForBoard(itemsContainer, boardName)

        // Ajouter les composants au layout du tableau
        boardLayout.addView(headerLayout)
        boardLayout.addView(addItemButton)
        boardLayout.addView(itemsContainer)

        boardContainer.addView(boardLayout)
    }

    private fun addItemToBoard(boardLayout: LinearLayout, boardName: String) {
        val newItem = "Nouvel Item " + (getItemsForBoard(boardName).size + 1)
        val itemsContainer = boardLayout.getChildAt(2) as LinearLayout
        addItemView(itemsContainer, newItem)

        val items = getItemsForBoard(boardName)
        items.add(newItem)
        saveItemsForBoard(boardName, items)
    }

    private fun loadItemsForBoard(itemsContainer: LinearLayout, boardName: String) {
        val items = getItemsForBoard(boardName)
        for (item in items) {
            addItemView(itemsContainer, item)
        }
    }

    private fun addItemView(itemsContainer: LinearLayout, itemText: String) {
        val itemView = TextView(this).apply {
            text = itemText
            textSize = 16f
            setPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 8, 0, 8)
            }
            setBackgroundResource(android.R.color.darker_gray)
        }
        itemsContainer.addView(itemView)
    }

    private fun deleteBoard(boardName: String, boardLayout: LinearLayout) {
        // 1️⃣ Supprimer la vue du tableau
        boardContainer.removeView(boardLayout)

        // 2️⃣ Supprimer le tableau des SharedPreferences
        val boards = getBoards()
        boards.remove(boardName)
        saveBoards(boards)

        // 3️⃣ Supprimer les items associés au tableau des SharedPreferences
        sharedPreferences.edit().remove("$boardName-items").apply()

        Toast.makeText(this, "Tableau '$boardName' supprimé", Toast.LENGTH_SHORT).show()
    }

    private fun getBoards(): MutableList<String> {
        val boardsSet = sharedPreferences.getStringSet("boards", emptySet()) ?: emptySet()
        return boardsSet.toMutableList()
    }

    private fun saveBoards(boards: List<String>) {
        sharedPreferences.edit().putStringSet("boards", boards.toSet()).apply()
    }

    private fun getItemsForBoard(boardName: String): MutableList<String> {
        val itemsSet = sharedPreferences.getStringSet("$boardName-items", emptySet()) ?: emptySet()
        return itemsSet.toMutableList()
    }

    private fun saveItemsForBoard(boardName: String, items: List<String>) {
        sharedPreferences.edit().putStringSet("$boardName-items", items.toSet()).apply()
    }
}
