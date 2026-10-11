package com.kim.oraclemarie.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kim.oraclemarie.data.model.Card
import com.kim.oraclemarie.data.model.MarieQuote
import com.kim.oraclemarie.data.model.Prayer
import com.kim.oraclemarie.data.model.SingleDraw
import com.kim.oraclemarie.data.model.ThreeDraw
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

@Database(
    entities = [Card::class, Prayer::class, MarieQuote::class, SingleDraw::class, ThreeDraw::class],
    version = 6,
    exportSchema = false
)
abstract class OracleDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao
    abstract fun prayerDao(): PrayerDao
    abstract fun marieQuoteDao(): MarieQuoteDao
    abstract fun singleDrawDao(): SingleDrawDao
    abstract fun threeDrawDao(): ThreeDrawDao

    companion object {
        @Volatile private var INSTANCE: OracleDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE daily_draws ADD COLUMN userNote TEXT")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS daily_draws")
                db.execSQL("""
                    CREATE TABLE single_draws (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        cardId INTEGER NOT NULL,
                        selectedTheme TEXT NOT NULL,
                        userNote TEXT NOT NULL DEFAULT ''
                    )
                """.trimIndent())
                db.execSQL("""
                    CREATE TABLE three_draws (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        card1Id INTEGER NOT NULL,
                        card2Id INTEGER NOT NULL,
                        card3Id INTEGER NOT NULL,
                        selectedTheme TEXT NOT NULL,
                        userNote TEXT NOT NULL DEFAULT ''
                    )
                """.trimIndent())
            }
        }

        // Repopulate content tables from updated JSON (translations added) — preserves user draws/journal
        private fun createMigration3to4(context: Context) = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DELETE FROM cards")
                db.execSQL("DELETE FROM marie_quotes")
                db.execSQL("DELETE FROM prayers")

                val cardsJson = context.assets.open("cards_data.json").bufferedReader().readText()
                val cardsArray = JSONArray(cardsJson)
                val cardStmt = db.compileStatement(
                    "INSERT INTO cards (id,nameFr,nameEn,keyword,keywordEn,imageName,partNumber," +
                    "situationFr,situationEn,conseilFr,conseilEn,amourFr,amourEn,conseilAmourFr,conseilAmourEn," +
                    "travailFr,travailEn,conseilTravailFr,conseilTravailEn,santeFr,santeEn,conseilSanteFr,conseilSanteEn," +
                    "familleFr,familleEn,conseilFamilleFr,conseilFamilleEn) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                )
                for (i in 0 until cardsArray.length()) {
                    val o = cardsArray.getJSONObject(i)
                    cardStmt.apply {
                        bindLong(1, o.getInt("id").toLong())
                        bindString(2, o.getString("nameFr"))
                        bindString(3, o.getString("nameEn"))
                        bindString(4, o.getString("keyword"))
                        bindString(5, o.getString("keywordEn"))
                        bindString(6, o.getString("imageName"))
                        bindLong(7, o.getInt("partNumber").toLong())
                        bindString(8, o.getString("situationFr"))
                        bindString(9, o.getString("situationEn"))
                        bindString(10, o.getString("conseilFr"))
                        bindString(11, o.getString("conseilEn"))
                        bindString(12, o.getString("amourFr"))
                        bindString(13, o.getString("amourEn"))
                        bindString(14, o.getString("conseilAmourFr"))
                        bindString(15, o.getString("conseilAmourEn"))
                        bindString(16, o.getString("travailFr"))
                        bindString(17, o.getString("travailEn"))
                        bindString(18, o.getString("conseilTravailFr"))
                        bindString(19, o.getString("conseilTravailEn"))
                        bindString(20, o.getString("santeFr"))
                        bindString(21, o.getString("santeEn"))
                        bindString(22, o.getString("conseilSanteFr"))
                        bindString(23, o.getString("conseilSanteEn"))
                        bindString(24, o.getString("familleFr"))
                        bindString(25, o.getString("familleEn"))
                        bindString(26, o.getString("conseilFamilleFr"))
                        bindString(27, o.getString("conseilFamilleEn"))
                        executeInsert()
                        clearBindings()
                    }
                }

                val quotesJson = context.assets.open("quotes_data.json").bufferedReader().readText()
                val quotesArray = JSONArray(quotesJson)
                val quoteStmt = db.compileStatement(
                    "INSERT INTO marie_quotes (id,textFr,textEn,category) VALUES (?,?,?,?)"
                )
                for (i in 0 until quotesArray.length()) {
                    val o = quotesArray.getJSONObject(i)
                    quoteStmt.apply {
                        bindLong(1, o.getInt("id").toLong())
                        bindString(2, o.getString("textFr"))
                        bindString(3, o.getString("textEn"))
                        bindString(4, o.getString("category"))
                        executeInsert()
                        clearBindings()
                    }
                }

                val prayersJson = context.assets.open("prayers_data.json").bufferedReader().readText()
                val prayersArray = JSONArray(prayersJson)
                val prayerStmt = db.compileStatement(
                    "INSERT INTO prayers (id,titleFr,titleEn,theme,type,textFr,textEn,durationMinutes) VALUES (?,?,?,?,?,?,?,?)"
                )
                for (i in 0 until prayersArray.length()) {
                    val o = prayersArray.getJSONObject(i)
                    prayerStmt.apply {
                        bindLong(1, o.getInt("id").toLong())
                        bindString(2, o.getString("titleFr"))
                        bindString(3, o.getString("titleEn"))
                        bindString(4, o.getString("theme"))
                        bindString(5, o.getString("type"))
                        bindString(6, o.getString("textFr"))
                        bindString(7, o.getString("textEn"))
                        bindLong(8, o.getInt("durationMinutes").toLong())
                        executeInsert()
                        clearBindings()
                    }
                }
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("UPDATE prayers SET titleFr = 'Je Vous Salue Marie — Version Âme' WHERE id = 1")
                db.execSQL("UPDATE prayers SET titleFr = 'Memorare — Version Contemporaine' WHERE id = 8")
            }
        }

        private fun createMigration5to6(context: Context) = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DELETE FROM prayers")
                val prayersJson = context.assets.open("prayers_data.json").bufferedReader().readText()
                val prayersArray = JSONArray(prayersJson)
                val prayerStmt = db.compileStatement(
                    "INSERT INTO prayers (id,titleFr,titleEn,theme,type,textFr,textEn,durationMinutes) VALUES (?,?,?,?,?,?,?,?)"
                )
                for (i in 0 until prayersArray.length()) {
                    val o = prayersArray.getJSONObject(i)
                    prayerStmt.apply {
                        bindLong(1, o.getInt("id").toLong())
                        bindString(2, o.getString("titleFr"))
                        bindString(3, o.getString("titleEn"))
                        bindString(4, o.getString("theme"))
                        bindString(5, o.getString("type"))
                        bindString(6, o.getString("textFr"))
                        bindString(7, o.getString("textEn"))
                        bindLong(8, o.getInt("durationMinutes").toLong())
                        executeInsert()
                        clearBindings()
                    }
                }
            }
        }

        fun getInstance(context: Context): OracleDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    OracleDatabase::class.java,
                    "oracle_marie.db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, createMigration3to4(context.applicationContext), MIGRATION_4_5, createMigration5to6(context.applicationContext))
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { database ->
                                populateCards(context, database)
                                populateQuotes(context, database)
                                populatePrayers(context, database)
                            }
                        }
                    }
                })
                .build()
                .also { INSTANCE = it }
            }

        private suspend fun populateCards(context: Context, db: OracleDatabase) {
            val json = context.assets.open("cards_data.json").bufferedReader().readText()
            val array = JSONArray(json)
            val cards = (0 until array.length()).map { i ->
                val o = array.getJSONObject(i)
                Card(
                    id = o.getInt("id"),
                    nameFr = o.getString("nameFr"),
                    nameEn = o.getString("nameEn"),
                    keyword = o.getString("keyword"),
                    keywordEn = o.getString("keywordEn"),
                    imageName = o.getString("imageName"),
                    partNumber = o.getInt("partNumber"),
                    situationFr = o.getString("situationFr"),
                    situationEn = o.getString("situationEn"),
                    conseilFr = o.getString("conseilFr"),
                    conseilEn = o.getString("conseilEn"),
                    amourFr = o.getString("amourFr"),
                    amourEn = o.getString("amourEn"),
                    conseilAmourFr = o.getString("conseilAmourFr"),
                    conseilAmourEn = o.getString("conseilAmourEn"),
                    travailFr = o.getString("travailFr"),
                    travailEn = o.getString("travailEn"),
                    conseilTravailFr = o.getString("conseilTravailFr"),
                    conseilTravailEn = o.getString("conseilTravailEn"),
                    santeFr = o.getString("santeFr"),
                    santeEn = o.getString("santeEn"),
                    conseilSanteFr = o.getString("conseilSanteFr"),
                    conseilSanteEn = o.getString("conseilSanteEn"),
                    familleFr = o.getString("familleFr"),
                    familleEn = o.getString("familleEn"),
                    conseilFamilleFr = o.getString("conseilFamilleFr"),
                    conseilFamilleEn = o.getString("conseilFamilleEn")
                )
            }
            db.cardDao().insertAll(cards)
        }

        private suspend fun populateQuotes(context: Context, db: OracleDatabase) {
            val json = context.assets.open("quotes_data.json").bufferedReader().readText()
            val array = JSONArray(json)
            val quotes = (0 until array.length()).map { i ->
                val o = array.getJSONObject(i)
                MarieQuote(
                    id = o.getInt("id"),
                    textFr = o.getString("textFr"),
                    textEn = o.getString("textEn"),
                    category = o.getString("category")
                )
            }
            db.marieQuoteDao().insertAll(quotes)
        }

        private suspend fun populatePrayers(context: Context, db: OracleDatabase) {
            val json = context.assets.open("prayers_data.json").bufferedReader().readText()
            val array = JSONArray(json)
            val prayers = (0 until array.length()).map { i ->
                val o = array.getJSONObject(i)
                Prayer(
                    id = o.getInt("id"),
                    titleFr = o.getString("titleFr"),
                    titleEn = o.getString("titleEn"),
                    theme = o.getString("theme"),
                    type = o.getString("type"),
                    textFr = o.getString("textFr"),
                    textEn = o.getString("textEn"),
                    durationMinutes = o.getInt("durationMinutes")
                )
            }
            db.prayerDao().insertAll(prayers)
        }
    }
}