# SportsAnalytics - Performance & Tournament Management

A comprehensive, interactive Java desktop application designed to visualize and analyze sports performance metrics, manage facility routing, and generate optimal tournament schedules. This system acts as a real-world implementation of fundamental and advanced Data Structures and Algorithms (DSA) organized cleanly around core Course Outcomes (CO1 to CO6).

---

## 🛠️ Tech Stack & Prerequisites

* **Language:** Java ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=flat&logo=openjdk&logoColor=white) (JDK 21 )
* **Framework:** Java Swing / AWT (Built-in graphical user interface toolkit)
* **Data Storage:** Lightweight, portable CSV flat-files (e.g., `Athletes.csv`, `Matches.csv`, `Teams.csv`, `Metrics.csv`)

## 🚀 How the Project Works (Execution Flow)

The application operates as a localized desktop dashboard. It is fully compatible and works smoothly on standard IDEs like **VS Code** as well as **Eclipse**. Instead of utilizing heavy external databases or bulky server setups, it handles everything dynamically in system memory using optimized data structures and flushes changes cleanly to companion CSV spreadsheets.

1. **Clone the Repository:** Download or clone the project source code files directly to your local machine.
2. **Extract the Files:** Extract the downloaded project zip archive into your preferred workspace folder.
3. **Open and Build Environment:** Open the folder in **VS Code** or **Eclipse**. Wait for a few minutes so that the project dependencies build and the runtime environment gets fully loaded.
4. **Run the Project:** Locate and open the `Main.java` file, then click the Run button to launch the graphical dashboard and experience the project live.
5. **Interactive Workspace Navigation:** Upon launching, a visual sidebar enables seamless switching between six modular tabs, each mapping directly to specific algorithmic functions.
6. **Live Data Processing:** When data is added, updated, or manipulated via the input panels, the underlying data structure registers the change and instantly redraws the visual tree, graph, or timeline canvas on the right side of the screen.

---

## 📂 Core Modules & Detailed Functions

### 🟢 CO1: Trees & Balanced Structures
* **Visual Interface Reference:<img width="1917" height="1077" alt="image" src="https://github.com/user-attachments/assets/f5eadcec-57b4-4acd-8156-d3c8bd6d107d" />

* **Underlying DSA:** Binary Search Tree (BST), AVL Tree
* **Key Functions:**
  * **Runtime Athlete Input:** Allows users to add, update, or delete athlete entities using a unique ID, Name, Sport, Rating, and Team ID.
  * **Dynamic Tree Index View:** Renders an interactive diagram of the dataset structure. Users can toggle between a standard **BST** (indexed by ID) or a self-balancing **AVL Tree** (sorted by Rating) to witness real-time node balancing and rotations.
  * **Search & Traversals:** Provides an input field to look up athletes by ID with optimal $O(\log N)$ search complexity. Includes action buttons to execute and trace standard structural traversals: *In-Order*, *Pre-Order*, and *Post-Order*.

### 🔵 CO2: Multiway & Ranges
* **Visual Interface Reference:** <img width="512" height="288" alt="image" src="https://github.com/user-attachments/assets/298cee99-ac2d-49e6-8e62-1f74d26e997b" />

* **Underlying DSA:** B+ Tree, Segment Tree, Fenwick Tree (Binary Indexed Tree)
* **Key Functions:**
  * **Range Score Extraction:** Utilizes a **B+ Tree** index structure to quickly isolate and list multiple athlete match scores within a user-defined highest and lowest score boundary.
  * **Range Statistics Processor:** Uses a **Segment Tree** mapped by match positions (0 to 7) to run instantaneous continuous metrics—returning the exact *Sum*, *Minimum*, and *Maximum* scores for any highlighted match interval.
  * **Dynamic Score Editing:** Allows users to modify an individual match score, which instantly triggers updates up the Segment Tree and Fenwick Tree layers to preserve statistical accuracy.

### 🟣 CO3: Facility Graphs & MST
* **Visual Interface Reference:** <img width="512" height="288" alt="image" src="https://github.com/user-attachments/assets/679427bc-1d06-4ee6-9f0a-fb76e33aee5d" />

* **Underlying DSA:** Undirected Graph (Adjacency Matrix/List), Breadth-First Search (BFS), Depth-First Search (DFS), Prim's Algorithm (Minimum Spanning Tree)
* **Key Functions:**
  * **Network Modeling:** Represents camp training locations (e.g., Main Stadium, High-Performance Gym, Aquatic Complex) as physical vertices connected by weighted paths representing distances.
  * **Graph Traversals:** Features dedicated control controls to run **BFS** or **DFS** from a selected starting vertex, mapping out connections and detecting structural loops.
  * **Network Optimization:** Runs **Prim’s Algorithm** to calculate the Minimum Spanning Tree (MST), showing the most efficient way to link all facilities with the lowest possible cumulative path weight.
  * **Dynamic Input Mapping:** Enables users to inject custom vertices and edges into the canvas dynamically.

### 🟡 CO4: Shortest Paths & Scheduling
* **Visual Interface Reference:** <img width="512" height="288" alt="image" src="https://github.com/user-attachments/assets/62d8362f-d25e-4d4b-a58d-888644871c01" />

* **Underlying DSA:** Directed Acyclic Graph (DAG), Dijkstra's Algorithm, Bellman-Ford Algorithm, Floyd-Warshall All-Pairs Algorithm, Topological Sort
* **Key Functions:**
  * **Point-to-Point Routing:** Finds the absolute shortest route from a chosen Source Camp to a Destination Camp using **Dijkstra's** or **Bellman-Ford** routing algorithms.
  * **Global Network Mapping:** Computes and populates a complete **Floyd-Warshall All-Pairs Distance Matrix (km)** grid layout to showcase comprehensive distance trends across all recorded hubs.
  * **Conflict-Free Tournament Sequencing:** Leverages a **Topological Sort** on directed match brackets to generate a sequential tournament schedule timeline (e.g., Quarterfinal 1 $\rightarrow$ Quarterfinal 2), ensuring dependent fixtures execute in order without logical deadlocks.

### 🟤 CO5: Sorting & Benchmarks
* **Visual Interface Reference:** <img width="512" height="288" alt="image" src="https://github.com/user-attachments/assets/708b099b-a10e-4873-8f67-2fc04f6e9898" />

* **Underlying DSA:** Merge Sort, Quick Sort, Heap Sort, Counting Sort, Radix Sort
* **Key Functions:**
  * **Roster Arrangement:** Sorts active player rosters according to chosen attributes (like Athlete ID or Rating) using different selectable sorting techniques.
  * **Algorithmic Benchmarking Arena:** Houses a performance comparison engine where users can select a custom dataset size ($N$). Clicking 'Run Performance Test' profiles the exact execution time in milliseconds ($ms$) for all five sorting algorithms, displaying a data table and an analytical comparison chart.

### 🟠 CO6: Greedy vs Dynamic Programming
* **Visual Interface Reference:** <img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/ea565c19-a278-4a3e-9b84-5a8af4492e78" />

* **Underlying DSA:** Greedy Activity Selection, Fractional Knapsack, 0/1 Knapsack (Dynamic Programming), Longest Increasing Subsequence (LIS)
* **Key Functions:**
  * **Greedy Activity Selection Scheduler:** Maximizes facility productivity by scheduling non-overlapping training blocks (e.g., Morning Cardio, Weight Training, Yoga Recovery) across a chronological daily timeline rule. Clicking 'Run Greedy Selection' automatically isolates the maximum number of compatible events.
  * **Resource Allocation Panels:** Includes modular sub-options to demonstrate value-to-weight optimization via **Fractional Knapsack**, **0/1 Knapsack**, and streak patterns using an **LIS Visualizer**.
