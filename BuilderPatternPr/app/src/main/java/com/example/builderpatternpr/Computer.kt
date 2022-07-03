package com.example.builderpatternpr

class Computer private constructor(builder: ComputerBuilder) {
    //required parameters
    val hDD: String
    val rAM: String

    //optional parameters
    val isGraphicsCardEnabled: Boolean
    val isBluetoothEnabled: Boolean

    //Builder Class
    class ComputerBuilder(// required parameters
        val HDD: String, val RAM: String
    ) {
        // optional parameters
        var isGraphicsCardEnabled = false
        var isBluetoothEnabled = false
        fun setGraphicsCardEnabled(isGraphicsCardEnabled: Boolean): ComputerBuilder {
            this.isGraphicsCardEnabled = isGraphicsCardEnabled
            return this
        }

        fun setBluetoothEnabled(isBluetoothEnabled: Boolean): ComputerBuilder {
            this.isBluetoothEnabled = isBluetoothEnabled
            return this
        }

        fun build(): Computer {
            return Computer(this)
        }
    }

    init {
        hDD = builder.HDD
        rAM = builder.RAM
        isGraphicsCardEnabled = builder.isGraphicsCardEnabled
        isBluetoothEnabled = builder.isBluetoothEnabled
    }
}