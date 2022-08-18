/////////////////////////////////////////////////
// Listing 1: Hello World in aufgebohrter Form //
/////////////////////////////////////////////////

import java.time.Instant

fun main(args: Array<String>) {
    val name = if (args.size > 0) args[0] else "Leser"
    val leser = Gast(name, anrede = Anrede.werter)

    println("Hallo ${leser.anrede} $name!")
    println(leser)
}

enum class Anrede { Herr, Frau, werter }

data class Gast(val name: String,
                var zeit: Instant = Instant.now(),
                val anrede: Anrede?)


////////////////////////////////////////////
// Listing 2: Klassisch definierte Klasse //
////////////////////////////////////////////

class Rechteck {
  var breite: Int
  var laenge: Int
  val flaeche  // read-only Property als Getter
    get() = breite * laenge

  constructor (b: Int, l: Int) {
    breite = b
    laenge = l
  }

  constructor(seite: Int) : this(seite, seite)
}


/////////////////////////////////////////////////////
// Listing 3: Explizit offene Klassen und Methoden //
/////////////////////////////////////////////////////

open class ErweiterbareKlasse {
    open fun ueberschreibbareMethode(): String {
        return "Ich bin dein Vater."
    }
}

class AbgeleiteteKlasse : ErweiterbareKlasse() {
    override fun ueberschreibbareMethode() =
        "…ich dein Sohn!"
}


///////////////////////////////////////////////////
// Listing 4: Umgang mit potenziellen Nullwerten //
///////////////////////////////////////////////////

var nonNullable: String = "Oh"
var nullable: String? =
      if (Random.nextBoolean()) "yes."
      else null

// Die foglenden Zeilen würden Compile-Fehler erzeugen. 
//    nonNullable = null
//    nonNullable = nullable
//    nullable.trim()

// Kotlin Smartcasts helfen
if (nullable != null) {
    nonNullable = nullable.trim()
}

// Safe nagivation operator
nullable = nullable?.trim()

// Elvis operator
nonNullable = nullable ?: "no."

// Ich-weiß-es-besser™ 
//  NPE-Gefahr voraus!
nonNullable = nullable!!
nullable!!.trim()


///////////////////////////////////////////////////
// Listing 5: Extension erweitern fremde Klassen //
///////////////////////////////////////////////////

// Neue Extension-Funktion für Strings & null
fun String?.prepend(c: String): String {
    return if (this != null) c + this else c;
}

// Extension Property. Hier: ohne null
val String.exclaimed : String
    get() = "$this!"


var s: String? = null
s = s.prepend("Hi ") // fehlerfrei: "Hi "
println(s.exclaimed) // Dank smart cast: "Hi !"


/////////////////////////////////////////////
// Listing 6: Operatoren und Destructuring //
/////////////////////////////////////////////

data class Point(val x: Int, val y: Int, val z: Int) {
    operator fun plus(o: Int): Point {
        return Point(x + o, y + o, z + o)
    }
}

fun main() {
    val a = Point(7, 3, 1)
    val b = a + 2

    // Destructuring via component1()..3()
    val (w, l, h) = b
    println("$b: Raumvolumen = ${w*h*l}")
    // Point(x=9, y=5, z=3): Raumvolumen = 135
}


////////////////////////////////////////////////////
// Listing 7: Unterschiedliche Prüfungen mit when //
////////////////////////////////////////////////////

private fun describe(x: Number):String {
    val specialNums = setOf(13, 42, 99)
    return when (x) {
        1, 0 -> "binär"

        !is Int -> "kein Integer"

        in -9..9 -> "klein"
        in specialNums -> "besonders"
        !in -99..99 -> "ausserhalb des Rahmens"

        else -> "nichts von alledem"
    }
}

fun main() {
    listOf(0, 3.14, 42, 98).forEach {
        println("$it ist ${describe(it)}")
    }
    // 0 ist binär
    // 3.14 ist kein Integer
    // 42 ist besonders
    // 98 ist nichts von alledem
}


////////////////////////////////////////
// Listing 8: Funktionen in Variablen //
////////////////////////////////////////

val numbers = listOf(1,2,3,4,5,6)
val aggregator: (Int, Int) -> Int = 
     { acc: Int, number: Int -> acc + number }
val sum = numbers.fold(0, aggregator)

// Lambda-Parameter als Block; implizites `it`
val evenNums = numbers.dropWhile { 
    it % 2 == 0
} 


/////////////////////////////////////////////////
// Listing 9: Synatktischer Zucker für Lambdas //
/////////////////////////////////////////////////

// Dank syntaktischem Zucker für trailing lambdas
// sind die folgenden Statements alle gleichwertig:

view.setOnClickListener({ e -> doSomething(e) })

view.setOnClickListener() { e -> doSomething(e) }

view.setOnClickListener { e -> doSomething(e) }

view.setOnClickListener { doSomething(it) }


///////////////////////////////////////////////////////
// Listing 10: Pausierbare Funktionen mit Koroutinen //
///////////////////////////////////////////////////////

import kotlinx.coroutines.*

fun main() {
  runBlocking {        // Eröffnet blockierend einen CoroutineScope
    launch {           // Startet im Hintergrund eine Coroutine
                       // Dieser Lambda-Block wird in der Coroutine ausgeführt
      delay(999L)      // Suspendiert die Coroutine
      println("Welt!") // Ausgabe nach Fortsetzung
    }
    // Starte parallel 20x suspendierbare Funktionen
    repeat(20) { launch { printDot() } }
    print("Hallo") 
  }
  println("Ende")
}

// Suspendable function: Nur aus CoroutineScope aufrufbar 
suspend fun printDot() {
  delay(500L)
  print(".")
}


////////////////////////////////////////////////////////
// Listing 11: Koroutinen-Kommunikation über Channels //
////////////////////////////////////////////////////////

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

fun main() = runBlocking {
    val channel = Channel<Int>()  // Channel definieren
    launch {
        val result = someExpensiveComputation()
        channel.send(result) // Ergebnisse kommunizieren
        channel.close()      // Letztes Ergebnis signalisieren
    }
    // Via for-Schleife lesen der Channel-Werte bis zum close()
    for (y in channel) println(y)
}


////////////////////////////////////////////////
// Listing 12: Fehlerbehandlung in Coroutines //
////////////////////////////////////////////////

import kotlinx.coroutines.* 
 
fun main() {
  val handler = CoroutineExceptionHandler { _, exception ->
    println("CoroutineExceptionHandler got $exception")
  }
    
  val topLevelScope = CoroutineScope(Job())

  topLevelScope.launch(handler) {
    launch {
      throw RuntimeException("RuntimeException in nested coroutine")
    }
  }

  Thread.sleep(100)
}
