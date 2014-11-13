package com.soontobe.joinpay.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.soontobe.joinpay.PaymentSummaryAdapter;
import com.soontobe.joinpay.R;
import com.soontobe.joinpay.Utility;
import com.soontobe.joinpay.widget.PendingTransactionItemView;
import com.soontobe.joinpay.widget.PendingTransactionItemView.OnAcceptButtonClickListener;
import com.soontobe.joinpay.widget.PendingTransactionItemView.OnDeclineButtonClickListener;

public class HistoryFragment extends Fragment 
implements LoaderCallbacks<Void> {

	private OnFragmentInteractionListener mListener;
	private View mCurrentView;
	private ArrayList<ArrayList<String[]>> paymentInfoList;
	private LinearLayout mHistoryLayout;
	private ArrayList<PendingTransactionItemView> mPendingTIVList;
	
	private ArrayList<ArrayList<String []>> mPendingInfoList;
	private boolean newRecordAvailable;
	private ArrayList<String[]> newPaymentInfo;

	public HistoryFragment() {
		// Required empty public constructor
		newRecordAvailable = false;
		paymentInfoList = new ArrayList<ArrayList<String[]>>();
		mPendingInfoList = new ArrayList<ArrayList<String[]>>();
		mPendingTIVList = new ArrayList<PendingTransactionItemView>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	

	@Override
	public void onResume() {
		Log.d("Frag", "OnResume(");
		//checkPendingInfo();
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		if(mCurrentView == null)
			mCurrentView = inflater.inflate(R.layout.fragment_history, container, false);
		
		ViewGroup parent = (ViewGroup) mCurrentView.getParent(); 
		if(parent != null){
			parent.removeView(mCurrentView);
		}
		
		mHistoryLayout = (LinearLayout)mCurrentView.findViewById(R.id.history_view_pane_items);
		
		checkPendingInfo();
		
		return mCurrentView;
	}
	
	
	private void checkPendingInfo() {
		// TODO Auto-generated method stub
		if(mPendingInfoList.isEmpty()){
			return;
		}
		for(ArrayList<String []> pInfo: mPendingInfoList){
			addTransactionItem(pInfo);
		}
		mPendingInfoList.clear();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		
		//TODO:in case of second start
		LinearLayout historyItems = (LinearLayout) getActivity().findViewById(R.id.history_view_pane_items);
		int margin = 15;
		ListView lv;
		LinearLayout.MarginLayoutParams mlp;
		for (int i = 0;i < paymentInfoList.size();i++) {
			lv = new ListView(getActivity());
			PaymentSummaryAdapter adapter = new PaymentSummaryAdapter(getActivity(), paymentInfoList.get(i), true);
			lv.setAdapter(adapter);
			lv.setBackgroundColor(Color.rgb(0xff, 0xff, 0xff));
			historyItems.addView(lv, 0);
			Utility.setListViewHeightBasedOnChildren(lv);
			mlp = (LinearLayout.MarginLayoutParams) lv.getLayoutParams();
			mlp.setMargins(0, margin, 0, margin);
		}
		
	}
	
	/**
	 * Add a transaction note to history view
	 * @param info
	 */
	public void addTransactionItem(ArrayList<String[]> info){
		PendingTransactionItemView pItemView = new PendingTransactionItemView(getActivity());
		int index = mPendingTIVList.size();
		pItemView.setPaymentInfo(info);
		pItemView.setAcceptButtonClickListener(new OnPendingItemAcceptedListener(index));
		pItemView.setDeclineButtonClickListener(new OnPendingItemDeclinedListener(index));
		mPendingTIVList.add(pItemView);
		
		mHistoryLayout.addView(pItemView, 0);
	}
	
	/**
	 * Add pending transaction info to list
	 * @param info
	 */
	public void addPendingTransItem(ArrayList<String[]> info){
		mPendingInfoList.add(info);
	}
	
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}


	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

	@Override
	public Loader<Void> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Void> arg0, Void arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoaderReset(Loader<Void> arg0) {
		// TODO Auto-generated method stub

	}

	public void setNewRecordNotification(ArrayList<String []> newPaymentInfo) {
		paymentInfoList.add(newPaymentInfo);
	}
	
	private class OnPendingItemAcceptedListener implements OnAcceptButtonClickListener{
		private int index;
		
		public OnPendingItemAcceptedListener(int _index){
			index = _index;
		}
		
		@Override
		public void OnClick(View v) {
			// TODO Auto-generated method stub
			
			String confirmMsg = "Confirm the payment?";
			new AlertDialog.Builder(getActivity())
				.setMessage(confirmMsg)
				.setPositiveButton("OK", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mPendingTIVList.get(index).setAccepted();
					}
				})
				.setNegativeButton("Don\'t allow", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						return;
					}
				})
				.show();
			
		}
		
	}
	
	private class OnPendingItemDeclinedListener implements OnDeclineButtonClickListener{
		private int index;
		
		public OnPendingItemDeclinedListener(int _index){
			index = _index;
		}
		
		@Override
		public void OnClick(View v) {
			// TODO Auto-generated method stub
			mPendingTIVList.get(index).setDeclined();
		}
	}
}
