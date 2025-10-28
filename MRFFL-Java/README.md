# MRFFL Java - MR Fortran Finance Library (Java Implementation)

This is a Java implementation of the MRFFL (MR Fortran Finance Library), providing financial modeling capabilities including Time Value of Money (TVM) calculations, cashflow analysis, statistical functions, and US market data.

## Original Fortran Library

The original Fortran implementation is located in `../MRFFL/`. This Java port aims to maintain mathematical accuracy and functionality while providing an idiomatic Java API.

**Original Repository:** https://github.com/SachetCognition/FortranFinance

## Project Structure

```
MRFFL-Java/
├── src/
│   ├── main/java/com/mrffl/
│   │   ├── config/        # Foundation: type definitions and constants
│   │   ├── util/          # Utilities: percentages, bitset, solver
│   │   ├── stats/         # Statistical functions and distributions
│   │   ├── data/          # US market and economic data
│   │   ├── tvm/           # Time Value of Money solvers
│   │   ├── cashflow/      # Cashflow analysis and IRR
│   │   └── actuarial/     # Life tables and actuarial computations
│   └── test/java/com/mrffl/  # Unit tests
├── pom.xml                # Maven build configuration
└── README.md              # This file
```

## Building

This project uses Maven for build management:

```bash
# Build the project
mvn clean compile

# Run tests
mvn test

# Package as JAR
mvn package
```

## Implementation Status

### Phase 1: Foundation Layer ✅ COMPLETE
- ✅ `MrfflConfig` - Type definitions and constants
- ✅ `VarSets` - TVM variable bitset constants
- ✅ `PrtSets` - Print formatting bitset constants
- ✅ `Percentages` - Percentage conversion and calculation utilities

### Phase 2: Support Utilities ✅ COMPLETE
- ✅ `Bitset` - Bitset operations for variable tracking
- ✅ `Solver` - Numerical root-finding (bisection method) with status codes
- ✅ `Stats` - Random number generation, distributions, probit, and Brownian motion

### Phase 3: Data Modules 📅 PLANNED
- ⬜ `UsInflation` - Historical US inflation data
- ⬜ `UsMarkets` - Stock market and Treasury data

### Phase 4: Core Financial Logic 📅 PLANNED
- ⬜ `Tvm` - Advanced TVM solvers
- ⬜ `Cashflows` - Cashflow analysis and IRR
- ⬜ `Tvm12` - Calculator-style TVM
- ⬜ `UsTaxes` - US tax calculations
- ⬜ `LifeTable` - Actuarial life tables

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Testing

Unit tests are based on the original Fortran test suite located in `../MRFFL/unit_tests/`. Each Java module includes corresponding JUnit 5 tests to validate correctness against the Fortran implementation.

Run all tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=PercentagesTest
```

## Documentation

Full migration documentation is available in `../Java_Migration_Plan_MRFFL.md`.

## License

Same as the original MRFFL library. See the original repository for license details.

## Migration Progress

See `../Java_Migration_Plan_MRFFL.md` for the complete migration plan and progress tracking.
