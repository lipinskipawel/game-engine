# Benchmark

## How to run benchmark
Execute following commands
```bash
cd engine-benchmar
```
```bash
mvn clean package
```
```bash
java -jar target/benchmarks.jar
```
## Results
|SimpleMatrix          |Score    |
|----------------------|---------|
|multiply              |0.15     |
|multiply element wise |7.535    |
|add                   |7.674    |
|subtract              |7.490    |
|transpose             |46909.410|
