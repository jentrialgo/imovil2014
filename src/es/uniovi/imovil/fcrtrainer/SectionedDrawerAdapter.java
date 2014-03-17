/*

Copyright 2014 Profesores y alumnos de la asignatura Informática Móvil de la EPI de Gijón

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package es.uniovi.imovil.fcrtrainer;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Adaptador que organiza la lista de elementos en secciones.
 * 
 */
final class SectionedDrawerAdapter extends BaseAdapter {

	public static class Group<K, T> {
		public K key;
		public T[] children = null;

		public Group(K key) {
			this.key = key;
		}
	}

	class ChildViewHolder {
		public TextView entryTextView;
	}

	class HeaderViewHolder {
		public TextView titleTextView;
	}

	private ArrayList<Group<String, Integer>> mGroups;
	private Context mContext;
	private int[] mGroupBaseIndexes;
	private LayoutInflater mInflater;
	private int mChildLayoutRes;
	private int mHeaderLayoutRes;

	public SectionedDrawerAdapter(Context context, int childLayoutRes,
			int headerLayoutRes, ArrayList<Group<String, Integer>> groups) {
		if (context == null || groups == null) {
			throw new IllegalArgumentException();
		}
		mContext = context;
		mGroups = groups;
		mInflater = LayoutInflater.from(context);
		mChildLayoutRes = childLayoutRes;
		mHeaderLayoutRes = headerLayoutRes;
		mGroupBaseIndexes = computeBaseIndexes();
	}

	@Override
	public int getCount() {
		// Cada grupo tiene su cabecera
		int maxGroupIndex = mGroupBaseIndexes.length - 1;
		return mGroupBaseIndexes[maxGroupIndex]
				+ mGroups.get(maxGroupIndex).children.length + 1;
	}

	@Override
	public Object getItem(int position) {
		int groupIndex = getGroupIndex(position);
		int childIndex = getChildIndex(groupIndex, position);
		if (childIndex == -1) {
			// Cabecera
			return mGroups.get(groupIndex).key;
		}
		return mGroups.get(groupIndex).children[childIndex];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		int groupIndex = getGroupIndex(position);
		if (getChildIndex(groupIndex, position) < 0)
			return false;
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int groupIndex = getGroupIndex(position);
		int childIndex = getChildIndex(groupIndex, position);
		if (childIndex < 0) {
			String header = mGroups.get(groupIndex).key;
			return getGroupView(header, convertView, parent);
		} else {
			int child = mGroups.get(groupIndex).children[childIndex];
			return getChildView(child, convertView, parent);
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		mGroupBaseIndexes = computeBaseIndexes();
	}

	private int getGroupIndex(int position) {
		int groupIndex = 1;
		for (; groupIndex < mGroupBaseIndexes.length; groupIndex++) {
			if (position < mGroupBaseIndexes[groupIndex]) {
				return groupIndex - 1;
			}
		}
		return groupIndex - 1;
	}

	private int getChildIndex(int groupIndex, int position) {
		return position - mGroupBaseIndexes[groupIndex] - 1;
	}

	public View getGroupView(String header, View convertView, ViewGroup parent) {
		View groupView = convertView;
		HeaderViewHolder viewHolder;
		if (groupView == null
			|| !(groupView.getTag() instanceof HeaderViewHolder)) {
			groupView = mInflater.inflate(mHeaderLayoutRes, parent, false);
			viewHolder = new HeaderViewHolder();
			viewHolder.titleTextView = (TextView) groupView
					.findViewById(R.id.title_text);
			groupView.setTag(viewHolder);
		}
		viewHolder = (HeaderViewHolder) groupView.getTag();
		viewHolder.titleTextView.setText(header);
		return groupView;
	}

	public View getChildView(int childId, View convertView, ViewGroup parent) {
		View childView = convertView;
		ChildViewHolder viewHolder;
		if (childView == null
			|| !(childView.getTag() instanceof ChildViewHolder)) {
			childView = mInflater.inflate(mChildLayoutRes, parent, false);
			viewHolder = new ChildViewHolder();
			viewHolder.entryTextView = (TextView) childView
					.findViewById(R.id.entry_text);
			childView.setTag(viewHolder);
		}
		viewHolder = (ChildViewHolder) childView.getTag();
		viewHolder.entryTextView.setText(mContext.getString(childId));
		return childView;
	}

	private int[] computeBaseIndexes() {
		if (mGroups.size() == 0) {
			return null;
		}
		int[] indexes = new int[mGroups.size()];
		indexes[0] = 0;

		int accumulatedItemCount = 0;
		for (int i = 1; i < indexes.length; i++) {
			accumulatedItemCount += mGroups.get(i - 1).children.length + 1;
			indexes[i] = accumulatedItemCount;
		}
		return indexes;
	}
}
