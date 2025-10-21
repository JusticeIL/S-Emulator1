package dto;

public enum ArchitectureGeneration {
    I {
        @Override
        public int getCost() {
            return 5; // Constant cost for Generation I
        }
    },
    II {
        @Override
        public int getCost() {
            return 100; // Constant cost for Generation II
        }
    },
    III {
        @Override
        public int getCost() {
            return 500; // Constant cost for Generation III
        }
    },
    IV {
        @Override
        public int getCost() {
            return 1000; // Constant cost for Generation IV
        }
    };

    /**
     * Abstract method to be implemented by each enum constant.
     * Returns a constant cost value associated with the architecture generation.
     */
    public abstract int getCost();
}