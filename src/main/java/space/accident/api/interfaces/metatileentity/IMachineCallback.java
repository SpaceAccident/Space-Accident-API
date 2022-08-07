package space.accident.api.interfaces.metatileentity;

public interface IMachineCallback<Machinetype extends IMetaTile> {
    Machinetype getCallbackBase();
    void setCallbackBase(Machinetype callback);
    Class<Machinetype> getType();
}
