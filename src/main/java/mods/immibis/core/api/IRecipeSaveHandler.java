package mods.immibis.core.api;

public interface IRecipeSaveHandler<StateType, DiffType> {
	public StateType save();
	public void apply(DiffType diff);
	public DiffType diff(StateType from, StateType to);
}
