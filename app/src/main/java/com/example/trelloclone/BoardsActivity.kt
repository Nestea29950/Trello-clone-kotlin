package com.example.trelloclone

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BoardsActivity : AppCompatActivity() {

    private lateinit var boardNameEditText: EditText
    private lateinit var backToHome: Button
    private lateinit var addBoardButton: Button
    private lateinit var boardContainer: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boards)

        // Initialisation des vues
        boardNameEditText = findViewById(R.id.boardNameEditText)
        addBoardButton = findViewById(R.id.addBoardButton)
        boardContainer = findViewById(R.id.boardContainer)
        backToHome = findViewById(R.id.backToHomeButton)
        // Initialisation de SharedPreferences
        sharedPreferences = getSharedPreferences("TrelloClone", Context.MODE_PRIVATE)

        // Afficher la liste des tableaux
        loadBoards()
        backToHome.setOnClickListener {
            // Création de l'intent pour lancer la nouvelle activité
            val intent = Intent(this, accueil::class.java)
            startActivity(intent) // Lancement de l'activité
        }
            // Ajouter un tableau au clic du bouton
        addBoardButton.setOnClickListener {
            val boardName = boardNameEditText.text.toString().trim()
            if (boardName.isNotEmpty()) {
                addBoard(boardName)
                boardNameEditText.text.clear()
            } else {
                Toast.makeText(this, "Le nom du tableau ne peut pas être vide", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addBoard(boardName: String) {
        val boards = getBoards()

        if (!boards.contains(boardName)) {
            boards.add(boardName)
            saveBoards(boards)
            addBoardView(boardName)
            Toast.makeText(this, "Tableau '$boardName' ajouté", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Ce tableau existe déjà", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadBoards() {
        val boards = getBoards()
        for (board in boards) {
            addBoardView(board)
        }
    }

    private fun addBoardView(boardName: String) {
        // Titre du tableau
        val boardTitle = TextView(this).apply {
            text = boardName
            textSize = 20f
            setPadding(16, 16, 16, 16)
        }

        // Bouton pour ajouter un item au tableau
        val addItemButton = Button(this).apply {
            text = "Ajouter un item"
            setOnClickListener {
                addItemToBoard(boardName)
            }
        }

        // Conteneur des items du tableau
        val itemContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            tag = boardName // On utilise le nom du tableau comme tag
        }

        // Charger les items déjà présents dans ce tableau
        loadItemsForBoard(itemContainer, boardName)

        // Conteneur global pour le tableau
        val boardLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(boardTitle)
            addView(addItemButton)
            addView(itemContainer)
        }

        boardContainer.addView(boardLayout)
    }

    private fun loadItemsForBoard(itemContainer: LinearLayout, boardName: String) {
        val items = getItemsForBoard(boardName)
        for (item in items) {
            addItemView(itemContainer, item)
        }
    }

    private fun addItemToBoard(boardName: String) {
        val itemName = boardName + " Item " + (getItemsForBoard(boardName).size + 1) // Générer un nom d'item par défaut

        // Demander à l'utilisateur un nom personnalisé pour l'item
        val itemNameEditText = EditText(this).apply {
            hint = "Entrez le nom de l'item"
        }

        val itemNameButton = Button(this).apply {
            text = "Ajouter l'item"
            setOnClickListener {
                val customItemName = itemNameEditText.text.toString().trim()

                if (customItemName.isNotEmpty()) {
                    val items = getItemsForBoard(boardName)
                    items.add(customItemName)
                    saveItemsForBoard(boardName, items)

                    // Rechercher le conteneur d'items du tableau
                    val itemContainer = boardContainer.findViewWithTag<LinearLayout>(boardName)
                    addItemView(itemContainer, customItemName)
                    Toast.makeText(this@BoardsActivity, "Item '$customItemName' ajouté", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@BoardsActivity, "Le nom de l'item ne peut pas être vide", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Afficher la boîte de saisie pour l'utilisateur
        val itemLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(itemNameEditText)
            addView(itemNameButton)
        }

        // Ajouter cette vue à l'interface (exemple : la position où l'ajouter dépend de la structure de ton layout)
        val boardLayout = boardContainer.findViewWithTag<LinearLayout>(boardName)
        boardLayout.addView(itemLayout)
    }


    private fun addItemView(itemContainer: LinearLayout, itemText: String) {
        val itemView = TextView(this).apply {
            text = itemText
            textSize = 16f
            setPadding(32, 8, 32, 8)
        }
        itemContainer.addView(itemView)
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
