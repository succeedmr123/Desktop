package org.freeplane.plugin.workspace.config.node;

import java.awt.Component;
import java.io.File;
import java.net.URI;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.freeplane.plugin.workspace.WorkspaceController;
import org.freeplane.plugin.workspace.WorkspaceUtils;
import org.freeplane.plugin.workspace.config.PopupMenus;
import org.freeplane.plugin.workspace.controller.IWorkspaceNodeEventListener;
import org.freeplane.plugin.workspace.controller.WorkspaceNodeEvent;
import org.freeplane.plugin.workspace.io.annotation.ExportAsAttribute;
import org.freeplane.plugin.workspace.model.AWorkspaceTreeNode;

public class PhysicalFolderNode extends AFolderNode implements IWorkspaceNodeEventListener {
	private static Icon FOLDER_OPEN_ICON = new ImageIcon(PhysicalFolderNode.class.getResource("/images/16x16/folder-orange_open.png"));
	private static final Icon FOLDER_CLOSED_ICON = new ImageIcon(PhysicalFolderNode.class.getResource("/images/16x16/folder-orange.png"));
		
//	private String folderPathProperty;
	private URI folderPath;
	
	

	private static String POPUP_KEY = "/filesystem_folder";

	public PhysicalFolderNode(String id) {
		super(id);
	}

//	@ExportAsAttribute("pathProperty")
//	public String getFolderPathProperty() {
//		return folderPathProperty;
//	}
//
//	public void setFolderPathProperty(String pathProperty) {
//		this.folderPathProperty = pathProperty;
//	}

	@ExportAsAttribute("path")
	public URI getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(URI folderPath) {
		this.folderPath = folderPath;
	}

	public static void refresh(AWorkspaceTreeNode node) {
		// if folder path is not correctly set
		if(node instanceof PhysicalFolderNode) {
			if (((PhysicalFolderNode) node).getFolderPath() == null) {
				return;
			}
	
			File folder;
			WorkspaceController controller = WorkspaceController.getController();
			try {
				folder = WorkspaceUtils.resolveURI(((PhysicalFolderNode)node).getFolderPath());
				if (folder.isDirectory()) {
					node.removeAllChildren();
					controller.getFilesystemReader().scanFileSystem(node, folder);
					controller.getWorkspaceModel().reload(node);
					
				}
	
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void initializePopup() {
		PopupMenus popupMenu = WorkspaceController.getController().getPopups();
		if (!isSystem()) {			
			popupMenu.registerPopupMenuNodeDefault(POPUP_KEY);
		}
		popupMenu.buildPopupMenu(POPUP_KEY);
	}

	public void handleEvent(WorkspaceNodeEvent event) {
		if (event.getType() == WorkspaceNodeEvent.MOUSE_RIGHT_CLICK) {			
			Component component = (Component) event.getBaggage();

			WorkspaceController.getController().getPopups()
					.showPopup(POPUP_KEY, component, event.getX(), event.getY());

		}
	}
	
	public boolean setIcons(DefaultTreeCellRenderer renderer) {
		renderer.setOpenIcon(FOLDER_OPEN_ICON);
		renderer.setClosedIcon(FOLDER_CLOSED_ICON);
		renderer.setLeafIcon(FOLDER_CLOSED_ICON);
		return true;
	}

	public String toString() {
		return this.getClass().getSimpleName() + "[id=" + this.getId() + ";name=" + this.getName() + ";path="
				+ this.getFolderPath() + "]";
	}

	public void refresh() {	
		if(getKey() == null) {
			return;
		}
		refresh(this);
		
	}
	
	protected AWorkspaceTreeNode clone(PhysicalFolderNode node) {		
		node.setFolderPath(getFolderPath());		
		return super.clone(node);
	}

	
	public AWorkspaceTreeNode clone() {
		PhysicalFolderNode node = new PhysicalFolderNode(getType());
		return clone(node);
	}
}