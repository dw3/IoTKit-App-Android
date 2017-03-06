package com.cylan.jiafeigou.cache.db.module;

import com.cylan.entity.jniCall.JFGAccount;
import com.cylan.jiafeigou.cache.db.view.IAccountAction;
import com.cylan.jiafeigou.cache.db.view.IAccountState;
import com.cylan.jiafeigou.cache.db.view.IAction;
import com.cylan.jiafeigou.cache.db.view.IEntity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by yanzhendong on 2017/3/4.
 */

@Entity(active = true)
public class Account implements IEntity<Account> {
    @Id
    private Long id;
    @Unique
    private String account;
    private String phone;
    private String token;
    private String alias;
    private boolean enablePush;
    private boolean enableSound;
    private String email;
    private boolean enableVibrate;
    private String photoUrl;
    private String action;
    private String state;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 335469827)
    private transient AccountDao myDao;

    public Account(JFGAccount account) {
        this.account = account.getAccount();
        this.phone = account.getPhone();
        this.token = account.getToken();
        this.alias = account.getAlias();
        this.enablePush = account.isEnablePush();
        this.enableSound = account.isEnableSound();
        this.email = account.getEmail();
        this.enableVibrate = account.isEnableVibrate();
        this.photoUrl = account.getPhotoUrl();
        this.action = IAccountAction.SAVED.action();
        this.state = IAccountState.SUCCESS.state();
    }

    @Generated(hash = 882125521)
    public Account() {
    }

    @Generated(hash = 1496705446)
    public Account(Long id, String account, String phone, String token, String alias,
                   boolean enablePush, boolean enableSound, String email, boolean enableVibrate,
                   String photoUrl, String action, String state) {
        this.id = id;
        this.account = account;
        this.phone = phone;
        this.token = token;
        this.alias = alias;
        this.enablePush = enablePush;
        this.enableSound = enableSound;
        this.email = email;
        this.enableVibrate = enableVibrate;
        this.photoUrl = photoUrl;
        this.action = action;
        this.state = state;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean getEnablePush() {
        return this.enablePush;
    }

    public void setEnablePush(boolean enablePush) {
        this.enablePush = enablePush;
    }

    public boolean getEnableSound() {
        return this.enableSound;
    }

    public void setEnableSound(boolean enableSound) {
        this.enableSound = enableSound;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getEnableVibrate() {
        return this.enableVibrate;
    }

    public void setEnableVibrate(boolean enableVibrate) {
        this.enableVibrate = enableVibrate;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public String getAction() {
        return this.action;
    }


    @Override
    public Account setAction(IAction action) {
        this.action = action.action();
        return this;
    }

    @Override
    public String ACTION() {
        return IAction.BaseAction.$(this.action, IAction.BaseAction.class).ACTION();
    }

    public Account setAction(String action) {
        this.action = action;
        return this;
    }

    public String getState() {
        return this.state;
    }

    public Account setState(String state) {
        this.state = state;
        return this;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1812283172)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAccountDao() : null;
    }


}