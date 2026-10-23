package com.quizmaster.service;

import com.quizmaster.model.Category;
import com.quizmaster.model.Difficulty;
import com.quizmaster.model.Question;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing the in-memory repository of 75+ multiple-choice questions
 * across all categories and difficulty levels.
 */
public class QuestionBankService {

    private final List<Question> questionBank = new ArrayList<>();

    public QuestionBankService() {
        loadQuestions();
    }

    private void loadQuestions() {
        // ==========================================
        // 1. GENERAL KNOWLEDGE (15 Questions: IDs 1-15)
        // ==========================================
        // Easy (1-5)
        addQuestion(1, "What is the capital city of France?",
                Category.GENERAL_KNOWLEDGE, Difficulty.EASY,
                Arrays.asList("Berlin", "Madrid", "Paris", "Rome"), 2,
                "Paris has been the capital of France since the 10th century.");

        addQuestion(2, "Which continent is the largest by land area?",
                Category.GENERAL_KNOWLEDGE, Difficulty.EASY,
                Arrays.asList("Africa", "Asia", "North America", "Europe"), 1,
                "Asia is the largest continent covering approximately 30% of the Earth's total land area.");

        addQuestion(3, "How many days are in a leap year?",
                Category.GENERAL_KNOWLEDGE, Difficulty.EASY,
                Arrays.asList("364", "365", "366", "367"), 2,
                "A leap year has 366 days because February has 29 days instead of 28.");

        addQuestion(4, "What is the official currency of Japan?",
                Category.GENERAL_KNOWLEDGE, Difficulty.EASY,
                Arrays.asList("Yuan", "Yen", "Won", "Baht"), 1,
                "The Japanese Yen (JPY) is the official currency of Japan.");

        addQuestion(5, "Who painted the Mona Lisa?",
                Category.GENERAL_KNOWLEDGE, Difficulty.EASY,
                Arrays.asList("Vincent van Gogh", "Pablo Picasso", "Leonardo da Vinci", "Claude Monet"), 2,
                "Leonardo da Vinci painted the Mona Lisa during the Italian Renaissance.");

        // Medium (6-10)
        addQuestion(6, "Which canal connects the Mediterranean Sea directly to the Red Sea?",
                Category.GENERAL_KNOWLEDGE, Difficulty.MEDIUM,
                Arrays.asList("Panama Canal", "Suez Canal", "Kiel Canal", "Erie Canal"), 1,
                "Opened in 1869, the Suez Canal connects the Mediterranean and Red Seas without locks.");

        addQuestion(7, "Who was the first woman to win a Nobel Prize?",
                Category.GENERAL_KNOWLEDGE, Difficulty.MEDIUM,
                Arrays.asList("Marie Curie", "Rosalind Franklin", "Mother Teresa", "Ada Lovelace"), 0,
                "Marie Curie won the Nobel Prize in Physics in 1903 and later in Chemistry in 1911.");

        addQuestion(8, "Which country has the longest coastline in the world?",
                Category.GENERAL_KNOWLEDGE, Difficulty.MEDIUM,
                Arrays.asList("Australia", "Russia", "Canada", "Norway"), 2,
                "Canada possesses the world's longest coastline, measuring over 202,080 kilometers.");

        addQuestion(9, "What was the ancient name for modern-day Istanbul?",
                Category.GENERAL_KNOWLEDGE, Difficulty.MEDIUM,
                Arrays.asList("Constantinople", "Mesopotamia", "Babylon", "Alexandria"), 0,
                "Istanbul was famously known as Constantinople until the early 20th century.");

        addQuestion(10, "In which year did the United Nations (UN) form?",
                Category.GENERAL_KNOWLEDGE, Difficulty.MEDIUM,
                Arrays.asList("1918", "1939", "1945", "1950"), 2,
                "The United Nations was established on October 24, 1945, after World War II.");

        // Hard (11-15)
        addQuestion(11, "Which African country was formerly known as Abyssinia?",
                Category.GENERAL_KNOWLEDGE, Difficulty.HARD,
                Arrays.asList("Eritrea", "Ethiopia", "Sudan", "Somalia"), 1,
                "Ethiopia was historically referenced as Abyssinia until the 20th century.");

        addQuestion(12, "Who was the architect who designed the glass pyramid at the Louvre?",
                Category.GENERAL_KNOWLEDGE, Difficulty.HARD,
                Arrays.asList("Frank Gehry", "I. M. Pei", "Le Corbusier", "Zaha Hadid"), 1,
                "I. M. Pei designed the iconic glass pyramid completed in 1989 at the Louvre Palace.");

        addQuestion(13, "What is the deepest known oceanic trench on Earth?",
                Category.GENERAL_KNOWLEDGE, Difficulty.HARD,
                Arrays.asList("Java Trench", "Mariana Trench", "Puerto Rico Trench", "Philippine Trench"), 1,
                "The Mariana Trench in the western Pacific reaches a maximum known depth of ~10,994 meters.");

        addQuestion(14, "Which treaty ended the Thirty Years' War in Europe in 1648?",
                Category.GENERAL_KNOWLEDGE, Difficulty.HARD,
                Arrays.asList("Treaty of Utrecht", "Peace of Westphalia", "Treaty of Versailles", "Treaty of Tordesillas"), 1,
                "The Peace of Westphalia established modern state sovereignty and religious tolerance boundaries.");

        addQuestion(15, "What is the only sovereign country that is also completely enclaved within South Africa?",
                Category.GENERAL_KNOWLEDGE, Difficulty.HARD,
                Arrays.asList("Eswatini", "Lesotho", "Botswana", "Namibia"), 1,
                "Lesotho is an independent enclaved nation entirely surrounded by South Africa.");

        // ==========================================
        // 2. SCIENCE (15 Questions: IDs 16-30)
        // ==========================================
        // Easy (16-20)
        addQuestion(16, "What gas do plants absorb from the atmosphere during photosynthesis?",
                Category.SCIENCE, Difficulty.EASY,
                Arrays.asList("Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen"), 2,
                "Plants absorb carbon dioxide (CO2) and release oxygen as a byproduct of photosynthesis.");

        addQuestion(17, "What is the chemical symbol for Gold?",
                Category.SCIENCE, Difficulty.EASY,
                Arrays.asList("Ag", "Au", "Fe", "Cu"), 1,
                "Au comes from the Latin word 'aurum', meaning shining dawn.");

        addQuestion(18, "Which planet in our solar system is known as the Red Planet?",
                Category.SCIENCE, Difficulty.EASY,
                Arrays.asList("Venus", "Mars", "Jupiter", "Saturn"), 1,
                "Mars appears reddish due to the high concentration of iron oxide (rust) on its surface.");

        addQuestion(19, "What is the fundamental unit of life in all living organisms?",
                Category.SCIENCE, Difficulty.EASY,
                Arrays.asList("Atom", "Tissue", "Cell", "Organ"), 2,
                "The cell is the basic structural, functional, and biological unit of all known organisms.");

        addQuestion(20, "At what temperature Celsius does pure water freeze at standard pressure?",
                Category.SCIENCE, Difficulty.EASY,
                Arrays.asList("0°C", "32°C", "100°C", "-10°C"), 0,
                "Water transitions from liquid to solid ice at 0 degrees Celsius (32 degrees Fahrenheit).");

        // Medium (21-25)
        addQuestion(21, "Which organelle is considered the powerhouse of eukaryotic cells?",
                Category.SCIENCE, Difficulty.MEDIUM,
                Arrays.asList("Ribosome", "Endoplasmic Reticulum", "Mitochondrion", "Golgi Apparatus"), 2,
                "Mitochondria generate most of the chemical energy required by the cell in the form of ATP.");

        addQuestion(22, "What is the speed of light in a vacuum approximately equal to?",
                Category.SCIENCE, Difficulty.MEDIUM,
                Arrays.asList("150,000 km/s", "300,000 km/s", "450,000 km/s", "3,000,000 km/s"), 1,
                "The speed of light in vacuum is approximately 299,792 km/s (often rounded to 300,000 km/s).");

        addQuestion(23, "What type of chemical bond involves the sharing of electron pairs between atoms?",
                Category.SCIENCE, Difficulty.MEDIUM,
                Arrays.asList("Ionic bond", "Covalent bond", "Hydrogen bond", "Metallic bond"), 1,
                "A covalent bond is formed when two atoms share valence electrons to achieve stable octets.");

        addQuestion(24, "Which blood vessels carry oxygenated blood away from the human heart to the body?",
                Category.SCIENCE, Difficulty.MEDIUM,
                Arrays.asList("Veins", "Capillaries", "Arteries", "Venules"), 2,
                "Arteries carry oxygenated blood away from the heart, with the aorta being the largest.");

        addQuestion(25, "What law of physics states that for every action, there is an equal and opposite reaction?",
                Category.SCIENCE, Difficulty.MEDIUM,
                Arrays.asList("Newton's First Law", "Newton's Second Law", "Newton's Third Law", "Hooke's Law"), 2,
                "Newton's Third Law of Motion states that mutual forces of action and reaction are equal in magnitude and opposite in direction.");

        // Hard (26-30)
        addQuestion(26, "Which subatomic particle has no electric charge and is located within the atomic nucleus?",
                Category.SCIENCE, Difficulty.HARD,
                Arrays.asList("Electron", "Proton", "Neutron", "Positron"), 2,
                "Neutrons possess zero net electrical charge and are bound in the nucleus with protons.");

        addQuestion(27, "What is the most abundant gas in Earth's atmosphere by volume?",
                Category.SCIENCE, Difficulty.HARD,
                Arrays.asList("Oxygen (O2)", "Nitrogen (N2)", "Argon (Ar)", "Carbon Dioxide (CO2)"), 1,
                "Nitrogen accounts for approximately 78.08% of Earth's atmosphere, followed by oxygen at 20.95%.");

        addQuestion(28, "In quantum mechanics, what principle asserts that one cannot simultaneously know the exact position and momentum of a particle?",
                Category.SCIENCE, Difficulty.HARD,
                Arrays.asList("Pauli Exclusion Principle", "Heisenberg Uncertainty Principle", "Schrodinger Equivalence", "Planck Postulate"), 1,
                "Heisenberg's Uncertainty Principle states that position and momentum cannot both be measured precisely at once.");

        addQuestion(29, "Which enzyme is primarily responsible for unwinding the DNA double helix during replication?",
                Category.SCIENCE, Difficulty.HARD,
                Arrays.asList("DNA Polymerase", "DNA Ligase", "DNA Helicase", "RNA Primase"), 2,
                "Helicases are enzymes that break hydrogen bonds between nucleotide base pairs to unzip the double helix.");

        addQuestion(30, "What is the SI unit of magnetic flux density?",
                Category.SCIENCE, Difficulty.HARD,
                Arrays.asList("Weber", "Tesla", "Henry", "Gauss"), 1,
                "The Tesla (T) is the SI unit of magnetic flux density (equivalent to one Weber per square meter).");

        // ==========================================
        // 3. COMPUTER SCIENCE (15 Questions: IDs 31-45)
        // ==========================================
        // Easy (31-35)
        addQuestion(31, "What does CPU stand for in computer systems?",
                Category.COMPUTER_SCIENCE, Difficulty.EASY,
                Arrays.asList("Central Processing Unit", "Computer Program Unit", "Core Power Utility", "Central Performance User"), 0,
                "CPU stands for Central Processing Unit, the primary hardware component that executes instructions.");

        addQuestion(32, "How many bits are in a single standard byte?",
                Category.COMPUTER_SCIENCE, Difficulty.EASY,
                Arrays.asList("4", "8", "16", "32"), 1,
                "One standard byte consists of exactly 8 bits, capable of representing 256 distinct values.");

        addQuestion(33, "Which data structure operates on a Last-In, First-Out (LIFO) order?",
                Category.COMPUTER_SCIENCE, Difficulty.EASY,
                Arrays.asList("Queue", "Stack", "Array", "Linked List"), 1,
                "A Stack is a LIFO structure where the last item pushed is the first item popped.");

        addQuestion(34, "What does HTTP stand for in web communication?",
                Category.COMPUTER_SCIENCE, Difficulty.EASY,
                Arrays.asList("HyperText Transfer Protocol", "High Tunnel Transmission Protocol", "Hyper Tool Transmission Process", "HyperText Terminal Protocol"), 0,
                "HTTP stands for HyperText Transfer Protocol, the protocol used for transmitting web pages.");

        addQuestion(35, "What radix or base does the binary number system use?",
                Category.COMPUTER_SCIENCE, Difficulty.EASY,
                Arrays.asList("Base 2", "Base 8", "Base 10", "Base 16"), 0,
                "The binary number system is a base-2 system using only the digits 0 and 1.");

        // Medium (36-40)
        addQuestion(36, "What is the average time complexity of searching an element in a balanced binary search tree?",
                Category.COMPUTER_SCIENCE, Difficulty.MEDIUM,
                Arrays.asList("O(1)", "O(n)", "O(log n)", "O(n log n)"), 2,
                "Balanced search trees (like AVL and Red-Black) maintain logarithmic height, yielding O(log n) search time.");

        addQuestion(37, "Which layer of the 7-layer OSI model does the Internet Protocol (IP) reside in?",
                Category.COMPUTER_SCIENCE, Difficulty.MEDIUM,
                Arrays.asList("Data Link Layer (Layer 2)", "Network Layer (Layer 3)", "Transport Layer (Layer 4)", "Application Layer (Layer 7)"), 1,
                "The Internet Protocol (IP) provides packet addressing and routing at Layer 3 (Network Layer).");

        addQuestion(38, "In relational databases, which normal form eliminates transitive functional dependencies?",
                Category.COMPUTER_SCIENCE, Difficulty.MEDIUM,
                Arrays.asList("First Normal Form (1NF)", "Second Normal Form (2NF)", "Third Normal Form (3NF)", "Boyce-Codd Normal Form (BCNF)"), 2,
                "Third Normal Form (3NF) requires 2NF plus the absence of transitive dependencies for non-prime attributes.");

        addQuestion(39, "Which scheduling algorithm assigns fixed time slices to processes in cyclic order?",
                Category.COMPUTER_SCIENCE, Difficulty.MEDIUM,
                Arrays.asList("First-Come, First-Served", "Shortest Job Next", "Round Robin", "Priority Scheduling"), 2,
                "Round Robin allocates a quantum/time slice to each process in turn, preventing starvation.");

        addQuestion(40, "What is the time complexity of the standard Merge Sort algorithm in the worst case?",
                Category.COMPUTER_SCIENCE, Difficulty.MEDIUM,
                Arrays.asList("O(n)", "O(n log n)", "O(n^2)", "O(log n)"), 1,
                "Merge Sort repeatedly halves the array and merges in linear time, guaranteeing O(n log n) in all cases.");

        // Hard (41-45)
        addQuestion(41, "Which algorithm is commonly used to find the shortest path from a single source node in a graph with non-negative edge weights?",
                Category.COMPUTER_SCIENCE, Difficulty.HARD,
                Arrays.asList("Bellman-Ford Algorithm", "Dijkstra's Algorithm", "Floyd-Warshall Algorithm", "Kruskal's Algorithm"), 1,
                "Dijkstra's algorithm greedily expands the shortest known distance to find single-source shortest paths.");

        addQuestion(42, "What condition is NOT one of the four Coffman conditions necessary for deadlock to occur?",
                Category.COMPUTER_SCIENCE, Difficulty.HARD,
                Arrays.asList("Mutual Exclusion", "Hold and Wait", "No Preemption", "Paging Inversion"), 3,
                "The 4 Coffman conditions are Mutual Exclusion, Hold and Wait, No Preemption, and Circular Wait.");

        addQuestion(43, "In public-key cryptography, which mathematical problem underlies the security of RSA?",
                Category.COMPUTER_SCIENCE, Difficulty.HARD,
                Arrays.asList("Discrete Logarithm", "Integer Factorization of large semi-primes", "Elliptic Curve Point Doubling", "Knapsack Problem"), 1,
                "RSA relies on the computational difficulty of factoring the product of two large prime numbers.");

        addQuestion(44, "Which parsing technique is commonly used in predictive top-down parsers without backtracking?",
                Category.COMPUTER_SCIENCE, Difficulty.HARD,
                Arrays.asList("LR(1)", "LALR", "LL(1)", "SLR"), 2,
                "LL(1) parsers read from Left to right, produce a Leftmost derivation, and use 1 token of lookahead.");

        addQuestion(45, "What is the space complexity of Depth-First Search (DFS) on a tree of height h and branching factor b?",
                Category.COMPUTER_SCIENCE, Difficulty.HARD,
                Arrays.asList("O(b^h)", "O(b * h)", "O(h)", "O(b)"), 2,
                "DFS only stores the nodes along the current path from root to leaf, requiring O(h) space.");

        // ==========================================
        // 4. MATHEMATICS (15 Questions: IDs 46-60)
        // ==========================================
        // Easy (46-50)
        addQuestion(46, "What is the value of 15% of 200?",
                Category.MATHEMATICS, Difficulty.EASY,
                Arrays.asList("20", "25", "30", "35"), 2,
                "15% of 200 is 0.15 * 200 = 30.");

        addQuestion(47, "What is the only even prime number?",
                Category.MATHEMATICS, Difficulty.EASY,
                Arrays.asList("0", "1", "2", "4"), 2,
                "2 is divisible only by 1 and itself, making it the only even prime number.");

        addQuestion(48, "What is the sum of interior angles in any Euclidean triangle?",
                Category.MATHEMATICS, Difficulty.EASY,
                Arrays.asList("90 degrees", "180 degrees", "270 degrees", "360 degrees"), 1,
                "The interior angles of any triangle on a flat plane always sum to 180 degrees.");

        addQuestion(49, "Solve for x: 3x - 7 = 14",
                Category.MATHEMATICS, Difficulty.EASY,
                Arrays.asList("5", "6", "7", "8"), 2,
                "Add 7 to both sides: 3x = 21, then divide by 3: x = 7.");

        addQuestion(50, "What is the square root of 144?",
                Category.MATHEMATICS, Difficulty.EASY,
                Arrays.asList("10", "11", "12", "14"), 2,
                "12 * 12 = 144, so the positive square root is 12.");

        // Medium (51-55)
        addQuestion(51, "What is the derivative of f(x) = 4x^3 with respect to x?",
                Category.MATHEMATICS, Difficulty.MEDIUM,
                Arrays.asList("12x^2", "7x^2", "12x^3", "4x^2"), 0,
                "By the power rule: d/dx(4x^3) = 4 * 3 * x^(3-1) = 12x^2.");

        addQuestion(52, "What is the probability of rolling a sum of 7 with two standard six-sided dice?",
                Category.MATHEMATICS, Difficulty.MEDIUM,
                Arrays.asList("1/12", "1/6", "5/36", "1/4"), 1,
                "There are 6 combinations that sum to 7 out of 36 outcomes: (1,6), (2,5), (3,4), (4,3), (5,2), (6,1). 6/36 = 1/6.");

        addQuestion(53, "If a circle has a radius of 7 units, what is its circumference? (Use pi = 22/7)",
                Category.MATHEMATICS, Difficulty.MEDIUM,
                Arrays.asList("22", "44", "88", "154"), 1,
                "Circumference = 2 * pi * r = 2 * (22/7) * 7 = 44.");

        addQuestion(54, "What is the determinant of the 2x2 matrix [[3, 2], [1, 4]]?",
                Category.MATHEMATICS, Difficulty.MEDIUM,
                Arrays.asList("10", "14", "8", "12"), 0,
                "Determinant = (3 * 4) - (2 * 1) = 12 - 2 = 10.");

        addQuestion(55, "What is the value of log10(1000)?",
                Category.MATHEMATICS, Difficulty.MEDIUM,
                Arrays.asList("1", "2", "3", "4"), 2,
                "10^3 = 1000, so log10(1000) = 3.");

        // Hard (56-60)
        addQuestion(56, "What is the integral of 1/x with respect to x for x > 0?",
                Category.MATHEMATICS, Difficulty.HARD,
                Arrays.asList("ln(x) + C", "-1/x^2 + C", "x + C", "e^x + C"), 0,
                "The antiderivative of 1/x for x > 0 is the natural logarithm ln(x) + C.");

        addQuestion(57, "How many distinct permutations can be formed from the letters of the word 'LEVEL'?",
                Category.MATHEMATICS, Difficulty.HARD,
                Arrays.asList("30", "60", "120", "24"), 0,
                "5! / (2! * 2!) = 120 / 4 = 30 distinct permutations.");

        addQuestion(58, "What is the value of e^(i * pi) according to Euler's formula?",
                Category.MATHEMATICS, Difficulty.HARD,
                Arrays.asList("0", "1", "-1", "i"), 2,
                "Euler's identity: e^(i*pi) + 1 = 0, so e^(i*pi) = -1.");

        addQuestion(59, "What is the dot product of vectors u = [2, -3, 1] and v = [4, 2, -1]?",
                Category.MATHEMATICS, Difficulty.HARD,
                Arrays.asList("1", "-1", "3", "0"), 0,
                "u · v = (2*4) + (-3*2) + (1*-1) = 8 - 6 - 1 = 1.");

        addQuestion(60, "In a geometric series with first term a = 6 and common ratio r = 1/3, what is the sum to infinity?",
                Category.MATHEMATICS, Difficulty.HARD,
                Arrays.asList("8", "9", "12", "18"), 1,
                "S_inf = a / (1 - r) = 6 / (1 - 1/3) = 6 / (2/3) = 9.");

        // ==========================================
        // 5. PROGRAMMING (15 Questions: IDs 61-75)
        // ==========================================
        // Easy (61-65)
        addQuestion(61, "Which keyword in Java is used to inherit a class?",
                Category.PROGRAMMING, Difficulty.EASY,
                Arrays.asList("implements", "extends", "inherits", "instanceof"), 1,
                "In Java, 'extends' is used for class inheritance, while 'implements' is for interfaces.");

        addQuestion(62, "What is the default initial value of an uninitialized boolean field in Java?",
                Category.PROGRAMMING, Difficulty.EASY,
                Arrays.asList("true", "false", "null", "0"), 1,
                "Primitive boolean instance variables default to false in Java.");

        addQuestion(63, "Which method is the required entry point for a standalone Java application?",
                Category.PROGRAMMING, Difficulty.EASY,
                Arrays.asList("public void run()", "public static void main(String[] args)", "public int start()", "private static void main()"), 1,
                "The JVM looks specifically for 'public static void main(String[] args)' to launch an application.");

        addQuestion(64, "Which data type in Java is used to store a single 16-bit Unicode character?",
                Category.PROGRAMMING, Difficulty.EASY,
                Arrays.asList("byte", "short", "char", "String"), 2,
                "'char' in Java is a 16-bit unsigned primitive type representing UTF-16 code units.");

        addQuestion(65, "What operator is used to compare whether two primitive values are equal in Java?",
                Category.PROGRAMMING, Difficulty.EASY,
                Arrays.asList("=", "==", "equals", "==="), 1,
                "The '==' equality operator compares primitive values, whereas '=' is the assignment operator.");

        // Medium (66-70)
        addQuestion(66, "Which Java Collection interface maintains elements in insertion order and allows positional indexing?",
                Category.PROGRAMMING, Difficulty.MEDIUM,
                Arrays.asList("java.util.Set", "java.util.List", "java.util.Queue", "java.util.Map"), 1,
                "List represents an ordered sequence where elements can be accessed by their integer index.");

        addQuestion(67, "What modifier prevents a Java class from being subclassed and a method from being overridden?",
                Category.PROGRAMMING, Difficulty.MEDIUM,
                Arrays.asList("static", "synchronized", "final", "abstract"), 2,
                "The 'final' modifier on a class prevents subclassing; on a method, it prevents overrides.");

        addQuestion(68, "Which exception is thrown when an application attempts to access an object reference that has null value?",
                Category.PROGRAMMING, Difficulty.MEDIUM,
                Arrays.asList("IllegalArgumentException", "ClassCastException", "NullPointerException", "IndexOutOfBoundsException"), 2,
                "NullPointerException is thrown when invoking a method or accessing a field on a null reference.");

        addQuestion(69, "In Java memory management, where are local variables and method call frames allocated?",
                Category.PROGRAMMING, Difficulty.MEDIUM,
                Arrays.asList("Heap Memory", "Stack Memory", "Metaspace", "Permanent Generation"), 1,
                "Stack memory manages execution frames and primitive local variables, while heap holds objects.");

        addQuestion(70, "What is the purpose of the 'finally' block in Java exception handling?",
                Category.PROGRAMMING, Difficulty.MEDIUM,
                Arrays.asList("Executes only if an exception is thrown", "Executes only if no exception is thrown", "Always executes regardless of whether an exception occurred", "Terminates the program immediately"), 2,
                "The 'finally' block always executes after try-catch (barring System.exit or JVM halt) for resource cleanup.");

        // Hard (71-75)
        addQuestion(71, "Which design pattern ensures a class has only one instance and provides a global access point to it?",
                Category.PROGRAMMING, Difficulty.HARD,
                Arrays.asList("Factory Pattern", "Singleton Pattern", "Observer Pattern", "Adapter Pattern"), 1,
                "The Singleton pattern restricts class instantiation to a single instance and provides a global accessor.");

        addQuestion(72, "What does the 'volatile' keyword guarantee when applied to a variable in Java?",
                Category.PROGRAMMING, Difficulty.HARD,
                Arrays.asList("Atomicity of compound operations", "Visibility of writes across threads and prevents instruction reordering", "Mutex lock synchronization on the object", "Automatic serialization of fields"), 1,
                "'volatile' ensures that reads and writes are directly synchronized with main memory and prevents reordering.");

        addQuestion(73, "What happens if two unequal objects produce the same hashCode() in a java.util.HashMap?",
                Category.PROGRAMMING, Difficulty.HARD,
                Arrays.asList("HashMap throws a HashCollisionException", "The old entry is overwritten", "A hash collision occurs and entries are stored in a linked list / red-black tree bucket", "The JVM crashes"), 2,
                "HashMap resolves collisions via bucket chaining using linked lists, transitioning to balanced trees if buckets grow large.");

        addQuestion(74, "Which garbage collector in modern Java uses region-based memory layout and aims for predictable pause times?",
                Category.PROGRAMMING, Difficulty.HARD,
                Arrays.asList("Serial GC", "Parallel GC", "G1 (Garbage-First) GC", "CMS (Concurrent Mark Sweep)"), 2,
                "G1 GC partitions the heap into equal-sized regions and targets low, predictable pauses.");

        addQuestion(75, "In Java Functional Programming, what is the key difference between Intermediate and Terminal Stream operations?",
                Category.PROGRAMMING, Difficulty.HARD,
                Arrays.asList("Intermediate operations execute eagerly; Terminal operations are lazy", "Intermediate operations return a new Stream and are lazy; Terminal operations trigger evaluation and produce a result", "Intermediate operations can only be called once", "Terminal operations cannot consume data"), 1,
                "Intermediate operations (like filter, map) are lazily piped until a terminal operation (like collect, count) triggers pipeline processing.");
    }

    private void addQuestion(int id, String text, Category cat, Difficulty diff, List<String> options, int correctIdx, String explanation) {
        questionBank.add(new Question(id, text, cat, diff, options, correctIdx, explanation));
    }

    public List<Question> getAllQuestions() {
        return Collections.unmodifiableList(questionBank);
    }

    public List<Question> getQuestionsByCategory(Category category) {
        if (category == null) return Collections.emptyList();
        return questionBank.stream()
                .filter(q -> q.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Question> getQuestions(Category category, Difficulty difficulty) {
        if (category == null || difficulty == null) return Collections.emptyList();
        return questionBank.stream()
                .filter(q -> q.getCategory() == category && q.getDifficulty() == difficulty)
                .collect(Collectors.toList());
    }

    public int getAvailableQuestionCount(Category category, Difficulty difficulty) {
        return getQuestions(category, difficulty).size();
    }

    public boolean hasSufficientQuestions(Category category, Difficulty difficulty, int requestedCount) {
        return getAvailableQuestionCount(category, difficulty) >= requestedCount;
    }

    public List<Question> getQuestions(Category category, Difficulty difficulty, int count) {
        List<Question> matching = getQuestions(category, difficulty);
        if (matching.isEmpty()) {
            return Collections.emptyList();
        }
        if (matching.size() <= count) {
            return new ArrayList<>(matching);
        }
        // Shuffle or pick first `count`
        List<Question> copy = new ArrayList<>(matching);
        Collections.shuffle(copy);
        return copy.subList(0, count);
    }
}
