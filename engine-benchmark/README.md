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
|SimpleMatrix          |Score    |Units|
|----------------------|---------|-----|
|multiply              |0.15     |ops/s|
|multiply element wise |7.535    |ops/s|
|add                   |7.674    |ops/s|
|subtract              |7.490    |ops/s|
|transpose             |46909.410|ops/s|

|NDMatrix              |Score        |Units|
|----------------------|-------------|-----|
|multiply              |0.091        |ops/s|
|multiply element wise |0.093        |ops/s|
|add                   |428216054.781|ops/s|
|subtract              |432423385.879|ops/s|
|transpose             |431155941.319|ops/s|
