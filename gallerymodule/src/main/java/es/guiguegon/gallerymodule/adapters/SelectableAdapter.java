package es.guiguegon.gallerymodule.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import java.util.ArrayList;
import java.util.List;

public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private SparseBooleanArray selectedItems;

    public SelectableAdapter() {
        selectedItems = new SparseBooleanArray();
    }

    /*
     * Indicates if the item at position position is selected
     * @param position Position of the item to check
     * @return true if the item is selected, false otherwise
     */
    public boolean isSelected(int position) {
        return getSelectedItemsPosition().contains(position);
    }

    /**
     * Toggle the selection status of the item at a given position
     *
     * @param position Position of the item to toggle the selection status for
     */
    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    /**
     * Toggle the selection status of the items position contained in ArrayList<Integer>
     *
     * @param selectedPositions Positions of the items to toggle the selection status for
     */
    public void setSelectedPositions(ArrayList<Integer> selectedPositions) {
        for (Integer integer : selectedPositions) {
            toggleSelection(integer);
        }
    }

    /**
     * Clear the selection status for all items
     */
    public void clearSelection() {
        List<Integer> selection = getSelectedItemsPosition();
        selectedItems.clear();
        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }

    public void selectAll(int position) {
        selectedItems.put(position, true);
        notifyItemChanged(position);
    }

    public void unSelectAll(int position) {
        selectedItems.delete(position);
        notifyItemChanged(position);
    }

    public void clearSelected() {
        selectedItems.clear();
    }

    /**
     * Count the selected items
     *
     * @return Selected items count
     */
    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    /**
     *
     */
    protected void notifySelectableAdapterItemInserted(int itemInsertedPosition) {
        List<Integer> selection = getSelectedItemsPosition();
        selectedItems.clear();
        for (Integer position : selection) {
            if (position < itemInsertedPosition) {
                selectedItems.put(position, true);
            } else {
                selectedItems.put(position + 1, true);
            }
        }
        notifyDataSetChanged();
    }

    /**
     *
     */
    protected void notifySelectableAdapterItemRemoved(int itemInsertedPosition) {
        List<Integer> selection = getSelectedItemsPosition();
        selectedItems.clear();
        for (Integer position : selection) {
            if (position < itemInsertedPosition) {
                selectedItems.put(position, true);
            } else {
                selectedItems.put(position - 1, true);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Indicates the list of selected items
     *
     * @return List of selected items ids
     */
    public ArrayList<Integer> getSelectedItemsPosition() {
        ArrayList<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); ++i) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }
}