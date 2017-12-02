import React from 'react';
import { connect } from 'dva';
import styles from './IndexPage.less';

function IndexPage() {
  return (
    <div className={styles.normal}>
      <div className={styles.overview} >
        &nbsp;
      </div>
      <div className={styles.trait}>
        特点
      </div>
      <div className={styles.project}>
        <div className={styles.title}>
          码云最有价值开源项目
        </div>
        <a className={styles.toproject} href="https://gitee.com/tywo45/t-io" target="_blank" >
        访问项目
        </a>
        <div className={styles.huiblock}>
        &nbsp;
        </div>
        <div className={styles.projectImg}>
          <img src="/mayun.png" alt="" />
        </div>
      </div>
      <div className={styles.meaning}>
        <div className={styles.title}>
          t-io诞生的意义
        </div>
        <div className={styles.paragraph}>
        旧时王谢堂前燕，飞入寻常百姓家----当年那些王谢贵族们才拥有的"百万级TCP长连接"应用，
        </div>
        <div className={styles.paragraph}>
        将因为t-io的诞生，纷纷飞入普通人家的屋檐下。
        </div>
        <div className={styles.saying}>
          <div className={styles.saycontent} >
            <img src="/touxiangpng.png" alt="" /> <span className={styles.name}>talent tan</span> <span className={styles.ps} >t-io创始人</span>
          </div>
        </div>
      </div>
    </div>
  );
}

IndexPage.propTypes = {
};

export default connect()(IndexPage);
