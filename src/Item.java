/*
 * Small helper for combo boxes: shows a label on screen but remembers the database id.
 * Made by: Mohd Dayan
 */
public class Item {
    public final int id;
    public final String label;

    public Item(int id, String label) {
        this.id = id;
        this.label = label;
    }

    /** id 0 means "nothing selected" (stored as NULL in the database). */
    public Integer idOrNull() {
        return id == 0 ? null : id;
    }

    @Override
    public String toString() {
        return label;
    }
}
