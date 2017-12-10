import React from 'react';
import { connect } from 'dva';
import styles from './IndexPage.less';

function IndexPage() {
  return (
    <div className={styles.normal}>
      <div className={styles.overview} >
        <div className={styles.iconBlock}>
          <img alt="" src="/homeLogo.png" />
        </div>
        <div className={styles.title1}>不仅仅是百万级即时通讯框架</div>
        <div className={styles.title2}>t-io是基于jdk aio实现的易学易用、稳定耐操、性能强悍、将多线程运用到极致、内置功能丰富的即时通讯框架。</div>
        <div className={styles.title3}>核心代码仅3000多行(2017年05月13号统计)</div>
      </div>
      <div className={styles.trait}>
        <div className={styles.traitImg}>
          <img src="/im.png" alt="" />
        </div>
        <div className={styles.traitInfo}>
          <div className={styles.traitInfoTitle}>
            t-io特点
          </div>
          <div className={styles.traitInfoItem}>
            <div className={styles.itemTitle}>极简洁、清晰、易懂的API</div>
            <div className={styles.itemInfo}>
              <p>原生态bytebuffer既减少学习成本，又减少各种中间对象的创建</p>
              <p>只需花30分钟学习helloworld，就能较好地掌握并实现</p>
            </div>
          </div>
          <div className={styles.traitInfoItem}>
            <div className={styles.itemTitle}>极震撼的性能</div>
            <div className={styles.itemInfo}>
              <p>单机轻松支持百万级tcp长连接，彻底甩开业界C1000K烦恼</p>
              <p>最高每秒可收发500万条业务消息，约165M</p>
            </div>
          </div>
          <div className={styles.traitInfoItem}>
            <div className={styles.itemTitle}>对开发人员极体贴的内置功能</div>
            <div className={styles.itemInfo}>
              <p>内置心跳检测</p>
              <p>内置心跳发送</p>
              <p>各种便捷的绑定API</p>
              <p>各种便捷的发送API</p>
              <p>一行代码拥有自动重连功能</p>
              <p>各项消息统计等功能，全部一键内置搞定，省却各种烦恼</p>
            </div>
          </div>
        </div>
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
