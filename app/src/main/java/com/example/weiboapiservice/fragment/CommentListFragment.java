package com.example.weiboapiservice.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.weiboapiservice.R;
import com.example.weiboapiservice.adapter.CommentListAdapter;
import com.example.weiboapiservice.model.WeiboComment;
import com.example.weiboapiservice.model.WeiboCommentList;
import com.example.weiboapiservice.retrofit.WeiboApiFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class CommentListFragment extends BaseListFragment {

    private List<WeiboComment> mCommentList = new ArrayList<>();

    public static CommentListFragment newInstance(long statusId) {
        CommentListFragment fragment = new CommentListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(STATUS_ID, statusId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initAdapter() {
        mBaseQuickAdapter = new CommentListAdapter(getActivity(), R.layout.item_comment_list, mCommentList);
    }

    @Override
    protected void getDataList() {
        mCompositeSubscription.add(WeiboApiFactory.createWeiboApi(null, access_token).getCommentList(mStatusId, pageIndex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WeiboCommentList>() {
                    @Override
                    public void call(WeiboCommentList weiboCommentList) {
                        List<WeiboComment> commentList = weiboCommentList.getComments();
                        if (pageIndex == 1) {
                            mCommentList.clear();
                            if (commentList == null || commentList.size() < 1) {
                                setAdapterEmpty();
                                return;
                            }

                            mCommentList.addAll(commentList);
                            mBaseQuickAdapter.notifyDataSetChanged();
                        } else {
                            if (commentList == null || commentList.size() < 1) {
                                mBaseQuickAdapter.notifyDataChangedAfterLoadMore(false);
                                View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_no_more_data,
                                        (ViewGroup) mRecyclerView.getParent(), false);
                                mBaseQuickAdapter.addFooterView(view);
                            } else {
                                mBaseQuickAdapter.notifyDataChangedAfterLoadMore(commentList, true);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        int code = ((HttpException) throwable).code();
                        Toast.makeText(getActivity(), "Error Code: " + code, Toast.LENGTH_SHORT).show();
                        if (pageIndex == 1) {
                            setAdapterEmpty();
                        }
                    }
                }));
    }

}
