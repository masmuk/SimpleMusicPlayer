
package com.erlangga.smplayer.core.android.vanilla;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.erlangga.smplayer.R;

public class PlaylistDialog extends DialogFragment
	implements DialogInterface.OnClickListener
{
	/**
	 * Default constructor as required by Gradle Release Lint
	 */
	public PlaylistDialog() {
	}

	/**
	 * Creates a new playlist dialog to assemble a playlist using an intent.
	 * Uses a static constructor method to satisfy Gradle Release Lint.
	 */
	public static PlaylistDialog newInstance(Callback callback, Intent intent, LibraryAdapter allSource) {
		PlaylistDialog pd = new PlaylistDialog();
		pd.mCallback = callback;
		pd.mData = pd.new Data();
		pd.mData.sourceIntent = intent;
		pd.mData.allSource = allSource;
		return pd;
	}

	/**
	 * A class implementing our callback interface
	 */
	private Callback mCallback;
	/**
	 * The data passed to the callback
	 */
	private PlaylistDialog.Data mData;
	/**
	 * Array of all found playlist names
	 */
	private String[] mItemName;
	/**
	 * Array of all found playlist values
	 */
	private long[] mItemValue;
	/**
	 * Index of 'create playlist' button
	 */
	private final int BUTTON_CREATE_PLAYLIST = 0;
	/**
	 * Our callback interface
	 */
	public interface Callback {
		void updatePlaylistFromPlaylistDialog(PlaylistDialog.Data data);
	}
	/**
	 * Our data structure
	 */
	public class Data {
		public String name;
		public long id;
		public Intent sourceIntent;
		public LibraryAdapter allSource;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Cursor cursor = Playlist.queryPlaylists(getActivity());
		if (cursor == null)
			return null;

		int count = cursor.getCount();
		mItemName = new String[1+count];
		mItemValue = new long[1+count];

		// Index 0 is always 'New Playlist...'
		mItemName[0] = getResources().getString(R.string.new_playlist);
		mItemValue[0] = -1;

		for (int i = 0 ; i < count; i++) {
			cursor.moveToPosition(i);
			mItemValue[1+i] = cursor.getLong(0);
			mItemName[1+i] = cursor.getString(1);
		}

		// All names are now known: we can show the dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.add_to_playlist)
			.setItems(mItemName, this);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case BUTTON_CREATE_PLAYLIST:
				PlaylistInputDialog newDialog = PlaylistInputDialog.newInstance(new PlaylistInputDialog.Callback() {
					@Override
					public void onSuccess(String input) {
						mData.id = -1;
						mData.name = input;
						mCallback.updatePlaylistFromPlaylistDialog(mData);
					}
				}, "", R.string.create);
				newDialog.show(getFragmentManager(), "PlaylistInputDialog");
				break;
			default:
				mData.id = mItemValue[which];
				mData.name = mItemName[which];
				mCallback.updatePlaylistFromPlaylistDialog(mData);
		}
		dialog.dismiss();
	}
}
