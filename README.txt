========================================================================
                      Concurrent-2026: Monte Carlo Pi Estimator
========================================================================

A high-performance concurrent Java application that estimates the value of Pi
using the Monte Carlo method.

This project demonstrates the performance differences between a sequential
(single-threaded) algorithm and an optimized parallel (multi-threaded)
implementation. It uses a dynamic thread-pool size scaled to your machine's
physical CPU cores and automatically visualizes the results.

------------------------------------------------------------------------
1. Mathematical Principle
------------------------------------------------------------------------
The Monte Carlo method estimates the value of Pi by plotting random coordinates
(x, y) in a square of side length 2 (from -1 to 1). A circle of radius r = 1 is
inscribed inside this square.

  Area of Circle = Pi * r^2 = Pi
  Area of Square = 2 * 2 = 4

The probability of a random point falling inside the circle is the ratio of
the circle's area to the square's area:

  P(inside) = Pi / 4

By generating N random coordinates and counting how many (M) satisfy the circle
equation (x^2 + y^2 <= 1), we approximate Pi using:

  Pi ≈ 4 * (M / N)

------------------------------------------------------------------------
2. Generated Visuals
------------------------------------------------------------------------
The application bypasses standard JVM GUI constraints (making it fully headless
and Docker-compatible) and directly outputs high-resolution PNG charts:

  * Monte Carlo Distribution Map (monte_carlo_visualization.png):
    A scatter plot of 10,000 points mapping coordinates falling inside (green)
    versus outside (blue) the circular boundary.

  * Performance Comparison Chart (performance_chart.png):
    A linear graph charting execution times (in seconds) of sequential versus
    parallel methods over expanding sample limits (ranging from 10 million
    up to 100 million points).

------------------------------------------------------------------------
3. Key Architectural Optimizations
------------------------------------------------------------------------
  * Zero Thread Contention: Uses ThreadLocalRandom in the parallel tasks.
    Standard java.util.Random shares a seed across threads using atomic CAS
    (Compare-And-Swap) operations, which severely bottlenecks multi-threaded
    performance. ThreadLocalRandom isolates random seeding per thread.

  * Non-blocking Work Distribution: Employs an ExecutorService thread pool
    combined with an ExecutorCompletionService to coordinate and compile results
    asynchronously.

  * Docker Headless Mode: Runs the JVM with -Djava.awt.headless=true so XChart
    can build, render, and write PNG files directly to disk without requiring
    an active graphical desktop display server (X11/Wayland).

------------------------------------------------------------------------
4. How the Code Works (Implementation Details)
------------------------------------------------------------------------
The project is structured into modular components targeting clear separation of
concerns (configuration, visual plotting, estimation algorithms, and orchestration):

  A. Core Logic & Architecture:
     * PiEstimator.java: A unified interface defining the estimatePi(long totalPoints)
       contract.
     * SequentialEstimator.java: Performs the computation on a single thread using
       a standard java.util.Random generator, sequentially checking if each generated
       coordinate satisfies the unit circle equation (x^2 + y^2 <= 1.0).
     * ParallelEstimator.java: Splits the total workload evenly among available
       CPU threads (totalPoints / numThreads). Spawns tasks inside an ExecutorService
       fixed thread pool. Crucially, each worker task (PiTask) uses ThreadLocalRandom
       to prevent synchronization contention. An ExecutorCompletionService processes
       and aggregates the results asynchronously.

  B. Configuration & Orchestration:
     * MonteCarloConfig.java: Acts as the central configurations file. It holds the
       different simulation sample sizes (10M to 100M points) and dynamically sets
       the thread count based on available CPU processors.
     * MonteCarloComparison.java: The application entrypoint. It runs a 10,000-point
       visualizer test, benchmark runs of both estimators, and saves the final
       performance graphs.

  C. Plotting & Rendering:
     * MonteCarloVisualizer.java: Generates a small 10,000 scatter point dataset,
       color-codes the positions (green inside, blue outside), draws a unit circle
       contour, and exports 'monte_carlo_visualization.png' directly to disk.

------------------------------------------------------------------------
5. Prerequisites
------------------------------------------------------------------------
Ensure you have the following installed:
  * Java Development Kit (JDK) 23
  * Apache Maven 3.9.x
  * (Optional) Docker

------------------------------------------------------------------------
6. Local Installation & Setup
------------------------------------------------------------------------
Follow these steps to clone, build, and execute the project natively:

  Step 1: Clone the Repository
    git clone <your-repository-url>
    cd Concurrent-2026

  Step 2: Build the Package
    Use Maven to resolve dependencies and build the shaded "fat" executable JAR:
    mvn clean package

    This generates the standalone package:
    target/MonteCarloPiEstimator-1.0-SNAPSHOT-jar-with-dependencies.jar

  Step 3: Run Natively
    java -jar target/MonteCarloPiEstimator-1.0-SNAPSHOT-jar-with-dependencies.jar

------------------------------------------------------------------------
7. Running with Docker
------------------------------------------------------------------------
The project contains a multi-stage Dockerfile that builds and compiles the Java
application within an isolated environment.

Because the application saves the output images inside its working directory,
you must mount a host directory volume to retrieve the charts.

  Step 1: Build the Docker Image
    docker build -t monte-carlo-pi .

  Step 2: Run the Container & Extract Images
    Run the container while mounting your current directory ($(pwd)) to the
    container's /app workspace directory. This ensures the generated PNG charts
    are instantly saved back to your local host machine:

    docker run --rm -v "$(pwd)":/app monte-carlo-pi

------------------------------------------------------------------------
8. Project Structure
------------------------------------------------------------------------
  ├── src/main/java/org/example/
  │   ├── MonteCarloComparison.java   # Main driver (orchestrates runs & saves chart)
  │   ├── MonteCarloConfig.java       # Global variables (sample sizes, threads)
  │   ├── MonteCarloVisualizer.java   # Plots & saves the 10,000-point map
  │   ├── PiEstimator.java            # Interface wrapper for estimators
  │   ├── SequentialEstimator.java    # Single-threaded estimator
  │   └── ParallelEstimator.java      # Multi-threaded concurrent estimator
  ├── Dockerfile                      # Multi-stage headless Docker configuration
  ├── pom.xml                         # Maven dependencies & Assembly plugin configs
  ├── monte_carlo_visualization.png   # Generated visualization plot
  └── performance_chart.png           # Generated execution performance graph

========================================================================
