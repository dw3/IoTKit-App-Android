package com.cylan.jiafeigou.n.mvp.contract.setting;

import com.cylan.jiafeigou.dp.DataPoint;
import com.cylan.jiafeigou.n.mvp.BasePresenter;
import com.cylan.jiafeigou.n.mvp.BaseView;

/**
 * Created by hunt on 16-5-26.
 */

public interface VideoAutoRecordContract {

    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresenter {

        /**
         * @param value {@link com.cylan.jiafeigou.dp.BaseValue#setValue(Object)}  }
         * @param id
         */
        public <T extends DataPoint> void updateInfoReq(T value, long id);

    }
}
