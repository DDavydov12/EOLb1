import kotlin.math.pow
import kotlin.random.Random

data class Value(val binaryStr: String, val functionResult: Int)

val minValue = -5
val maxValue = 58

val populationSize = 4
val chromosomeLength = 6

fun main() {
    val numberOfCrossovers = 2
    val numberOfMutations = 1

    val maxGenerations = 100

    var population = initializePopulation()

    for (generation in 0 until maxGenerations) {
        population = calculateResultsOfPopulation(population)

        println("Generation ${generation + 1}")

        var newPopulation = mutableListOf<Value>()

        repeat(numberOfCrossovers) {
            val parent1 = population.random()
            var parent2 = population.random()

            var count = 0

            while (parent1 == parent2) {
                if (count == 50) {
                    break
                }
                count++
                parent2 = population.random()
            }


            var child1: String
            var child2: String

            crossover(parent1.binaryStr, parent2.binaryStr, chromosomeLength).let { (c1, c2) ->
                child1 = c1
                child2 = c2
            }

            newPopulation += Value(child1, 0)
            newPopulation += Value(child2, 0)
        }

        repeat(numberOfMutations) {
            val randomKey = newPopulation.random()
            val mutatedChild = mutate(randomKey.binaryStr, chromosomeLength)
            newPopulation.remove(randomKey)
            newPopulation += Value(mutatedChild, 0)
        }

        newPopulation = calculateResultsOfPopulation(newPopulation).toMutableList()

        val allPopulations = (newPopulation + population)
        population = allPopulations.sortedByDescending { it.functionResult }.take(populationSize)

        println(population)
    }

    println(population)
}

fun initializePopulation(): List<Value> {
    val population = mutableListOf<Value>()

    while (population.size != populationSize) {
        val chromosome = Value(generateRandomChromosome(), 0)
        if (chromosome !in population) {
            population += chromosome
        }
    }

    return population
}

fun generateRandomChromosome(): String {
    val num = Random.nextInt(minValue, maxValue)
    return num.toBinaryStr()
}

fun Int.toBinaryStr() = Integer.toBinaryString(this).padStart(6, '0').takeLast(6)

fun crossover(parent1: String, parent2: String, chromosomeLength: Int): Pair<String, String> {
    val crossoverPoint = Random.nextInt(chromosomeLength)

    val child1 = parent1.substring(0, crossoverPoint) + parent2.substring(crossoverPoint)
    val child2 = parent2.substring(0, crossoverPoint) + parent1.substring(crossoverPoint)

    return child1 to child2
}

fun mutate(chromosome: String, chromosomeLength: Int): String {
    val chromosomeArray = chromosome.toCharArray()
    val randomIndex = Random.nextInt(chromosomeLength)
    chromosomeArray[randomIndex] = if (chromosomeArray[randomIndex] == '0') '1' else '0'
    return String(chromosomeArray)
}

fun calculateResultsOfPopulation(population: List<Value>): List<Value> {
    val newPop = mutableListOf<Value>()

    for ((chromosome, _) in population) {
        val decimalValue = chromosome.toInt(2)

        val functionResult = calculateFunctionValue(decimalValue - 10)

        newPop += Value(chromosome, functionResult)
    }

    return newPop
}

fun calculateFunctionValue(value: Int): Int {
    return 12 - (2 * value) - (8 * value.pow(2)) + (2 * value.pow(3))
}

fun Int.pow(nbr: Int): Int = toDouble().pow(nbr).toInt()
