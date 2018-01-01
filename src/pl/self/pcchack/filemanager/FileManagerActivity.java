package pl.self.pcchack.filemanager;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import pl.pcchack.activity.AbstractActivity;
import pl.pcchack.activity.ComponentUtil;
import pl.pcchack.activity.IActionListener;
import pl.pcchack.activity.ItemDescription;
import self.utility.ObjectUtil;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FileManagerActivity extends AbstractActivity implements OnItemClickListener,IActionListener<ItemDescription>{
	private ListView fileListView;
	private FileManagerAdapter adapter;
	private String path;
	private Deque<String> pathStack;
	private List<ItemDescription> fileActions;
	private File choosenFile;
	private static final String ROOT="/";
	private static final int FILE_EXECUTE=1,FILE_SEND=2,FILE_INFO=3;
	@Override
	protected void initFieldsAndServices(Bundle savedState) {
		pathStack = new ArrayDeque<String>();
		path = ROOT;
		
		fileListView = getListView(R.id.file_list_view);
		fileListView.setOnItemClickListener(this);
		
		fileActions = createFileActions();
		
		
		adapter = new FileManagerAdapter(this);
		adapter.addAll(new File(path).listFiles());
		
		
		fileListView.setAdapter(adapter);
		
	}
	
	private List<ItemDescription> createFileActions() {
		List<ItemDescription> fileActions = new ArrayList<ItemDescription>();
		Resources resources = getResources();
		fileActions.add(new ItemDescription(resources.getString(R.string.file_execute),"",R.drawable.ic_menu_forward).setId(FILE_EXECUTE));
		fileActions.add(new ItemDescription(resources.getString(R.string.file_send),"",R.drawable.ic_menu_send).setId(FILE_SEND));
		fileActions.add(new ItemDescription(resources.getString(R.string.file_info), "", android.R.drawable.ic_menu_info_details).setId(FILE_INFO));
		return fileActions;
	}
	
	@Override
	protected int getMainViewId() {
		return R.layout.main_layout;
	}

	@Override
	protected void saveDataInPreferences() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isKeepScreenOn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		choosenFile = adapter.getFile(position);	
		if(choosenFile.isDirectory())
			handleDirectoryClick(choosenFile);
		else
			handleFileClick(choosenFile);
	}
	
	private void handleFileClick(File choosenFile) {
		ComponentUtil.showMultipleChoiceDialog(this,
				getString(R.string.what_to_do,choosenFile.getName()),
				fileActions,this);
	}
	
	@Override
	public void onAction(ItemDescription action) {
		switch((int)action.getId()) {
		case FILE_EXECUTE:
			executeFile();
			break;
		case FILE_INFO:
			showInfo();
			break;
		case FILE_SEND:
			sendFile();
			break;
		
		}
	}
	
	private void executeFile() {
		Intent fileExecuteIntent = new Intent();
		fileExecuteIntent.setAction(Intent.ACTION_VIEW);
		fileExecuteIntent.setDataAndType(Uri.fromFile(choosenFile),ComponentUtil.findTypeForFile(choosenFile));
		fileExecuteIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(fileExecuteIntent);
		} catch(ActivityNotFoundException exception) {
			showToast(R.string.unsupported_file_format);
		}
	}
	private void showInfo() {
		showToast(choosenFile.getName());
	}
	private void sendFile() {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.setType(ComponentUtil.findTypeForFile(choosenFile));
		sendIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(choosenFile));
		startActivity(sendIntent);
	}
	
	
	private void handleDirectoryClick(File choosenFile) {
		File[] files = choosenFile.listFiles();
		if(ObjectUtil.nullOrEmpty(files)) {
			showToast(R.string.msg_directory_not_contains_files);
			return;
		}
		pathStack.push(path);
		path = choosenFile.getAbsolutePath();
		refreshAdapter(files);
	}
	
	@Override
	public void onBackPressed() {
		if(pathStack.isEmpty()) {
			super.onBackPressed();
			return;
		}
		path = pathStack.pop();
		File file = new File(path);
		refreshAdapter(file.listFiles());
		
	}

	private void refreshAdapter(File[] files) {
		adapter.clear();
		adapter.addAll(files);
	}

	

	
	
}
