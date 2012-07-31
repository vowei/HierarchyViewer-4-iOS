/**
 * Interface defining the application's command IDs.
 * Key bindings can be defined for specific commands.
 * To associate an action with a command, use IAction.setActionDefinitionId(commandId).
 *
 * @see org.eclipse.jface.action.IAction#setActionDefinitionId(String)
 */

package hierarchyviewerlib.actions;
public interface ICommandIds {

    public static final String CMD_SAVE_PICTURE = "hierarchyviewerlib.SaveHierarchyViewPicture";
    public static final String CMD_OPEN_IQUERY_DIALOG = "hierarchyviewerlib.iQueryCreatorDialog";
    public static final String CMD_OPEN_LOG_FILE = "hierarchyviewerlib.OpenLogFile";
    public static final String CMD_REFRESH_LOG_FILE = "hierarchyviewerlib.RefreshLogFile";
    public static final String CMD_OPEN_VIEWS = "hierarchyviewerlib.OpenView";
}
