package pl.self.pcchack.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class FileManagerAdapter extends ArrayAdapter<File> {
	private List<File> files;
	private LayoutInflater inflater;
	public FileManagerAdapter(Context context) {
		super(context,R.layout.g_list_item);
		files = new ArrayList<File>();
		inflater = LayoutInflater.from(getContext());
	}
	
	@Override
	public void add(File object) {
		super.add(object);
		files.add(object);
	}

	@Override
	public void addAll(Collection<? extends File> collection) {
		super.addAll(collection);
		List<File> directories = new ArrayList<File>(),realFiles = new ArrayList<File>();
		splitToFilesAndDirectories(collection,directories, realFiles);
		Collections.sort(directories);
		Collections.sort(realFiles);
		files.addAll(directories);
		files.addAll(realFiles);
	}
	private void splitToFilesAndDirectories(Iterable<? extends File> files,List<File> directories,List<File> realFiles) {
		for(File file : files) {
			if(file.isDirectory())
				directories.add(file);
			else
				realFiles.add(file);
		}
	}
	
	public void addAll(File...files) {
		addAll(Arrays.asList(files));
		
	}
	@Override
	public void clear() {
		super.clear();
		files.clear();
	}

	public File getFile(int position) {
		return files.get(position);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		File fileOnPosition = files.get(position);
		ViewHolder holder = null;
		if(row == null) {
			row = inflater.inflate(R.layout.g_list_item,parent,false);
			holder =createHolder(row);
			row.setTag(holder);
			
		} else 
			holder = (ViewHolder) row.getTag();
		fillHolderWithData(holder,fileOnPosition);
		return row;
	}
	private void fillHolderWithData(ViewHolder holder,File fileOnPosition) {
		holder.getIcon().setImageResource(getAppriopriateIconResource(fileOnPosition));
		holder.getItemName().setText(fileOnPosition.getName());
		holder.getItemDescription().setText(fileOnPosition.getAbsolutePath());
		holder.getToggleButton().setVisibility(View.GONE);
	}
	private ViewHolder createHolder(View row) {
		return new ViewHolder()
				.setIcon((ImageView)row.findViewById(R.id.g_item_image))
				.setItemName((TextView)row.findViewById( R.id.g_item_name))
				.setItemDescription((TextView)row.findViewById(R.id.g_item_description))
				.setToggleButton((ToggleButton)row.findViewById(R.id.g_item_check));
				
		
	}
	private int getAppriopriateIconResource(File file) {
		return file.isDirectory() ? R.drawable.ic_menu_archive : R.drawable.ic_menu_find_holo_light;
	}
	private class ViewHolder {
		private ImageView icon;
		private ToggleButton toggleButton;
		private TextView itemName;
		private TextView itemDescription;
		public ImageView getIcon() {
			return icon;
		}
		public ViewHolder setIcon(ImageView icon) {
			this.icon = icon;
			return this;
		}
		public ToggleButton getToggleButton() {
			return toggleButton;
		}
		public ViewHolder setToggleButton(ToggleButton toggleButton) {
			this.toggleButton = toggleButton;
			return this;
		}
		public TextView getItemName() {
			return itemName;
		}
		public ViewHolder setItemName(TextView itemName) {
			this.itemName = itemName;
			return this;
		}
		public TextView getItemDescription() {
			return itemDescription;
		}
		public ViewHolder setItemDescription(TextView itemDescription) {
			this.itemDescription = itemDescription;
			return this;
		}
		
		
		
	}
}
