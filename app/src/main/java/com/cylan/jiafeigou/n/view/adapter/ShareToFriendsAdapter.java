package com.cylan.jiafeigou.n.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.cylan.jiafeigou.R;
import com.cylan.jiafeigou.cache.db.module.FriendBean;
import com.cylan.jiafeigou.support.superadapter.IMulItemViewType;
import com.cylan.jiafeigou.support.superadapter.SuperAdapter;
import com.cylan.jiafeigou.support.superadapter.internal.SuperViewHolder;
import com.cylan.jiafeigou.widget.roundedimageview.RoundedImageView;

import java.util.List;


public class ShareToFriendsAdapter extends SuperAdapter<FriendBean> {

    private OnShareCheckListener listener;

    public interface OnShareCheckListener {
        void onCheck(boolean isCheck, SuperViewHolder holder, FriendBean item);
    }

    public void setOnShareCheckListener(OnShareCheckListener listener) {
        this.listener = listener;
    }

    public ShareToFriendsAdapter(Context context, List<FriendBean> items, IMulItemViewType<FriendBean> mulItemViewType) {
        super(context, items, mulItemViewType);
    }

    @Override
    public void onBind(final SuperViewHolder holder, int viewType, final int layoutPosition, final FriendBean item) {
        //如果没有备注名就显示别人昵称
        holder.setText(R.id.tv_friend_name, (item.markName == null || item.markName.equals("")) ? item.alias : item.markName);
        holder.setText(R.id.tv_friend_account, item.account);
        CheckBox checkBox = (CheckBox) holder.itemView.findViewById(R.id.checkbox_is_share_check);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.isCheckFlag = isChecked ? 1 : 2;
                if (listener != null) {
                    listener.onCheck(isChecked, holder, item);
                }
            }
        });

        RoundedImageView userImag = holder.getView(R.id.iv_userhead);
        //头像
        Glide.with(getContext()).load(item.iconUrl)
                .asBitmap()
                .error(R.drawable.icon_mine_head_normal)
                .placeholder(R.drawable.icon_mine_head_normal)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new BitmapImageViewTarget(userImag) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        userImag.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    @Override
    protected IMulItemViewType<FriendBean> offerMultiItemViewType() {
        return new IMulItemViewType<FriendBean>() {
            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public int getItemViewType(int position, FriendBean jfgFriendAccount) {
                return 0;
            }

            @Override
            public int getLayoutId(int viewType) {
                return R.layout.fragment_mine_share_to_friend_items;
            }
        };
    }
}
