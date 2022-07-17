package space.accident.api.interfaces.mte

interface IMachineCallback<MachineType : IMetaTileEntity?> {
    fun getCallbackBase(): MachineType
    fun setCallbackBase(callback: MachineType)
    fun getType(): Class<MachineType>
}