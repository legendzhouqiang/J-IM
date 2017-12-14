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
        <div className={styles.title2}><span style={{"fontWeight":"bold",fontSize:"15pt"}}>t-io：</span>让网络编程更简单和有趣</div>
        <div className={styles.title3}><span style={{"fontWeight":"bold",fontSize:"13pt"}}>简单：</span>极致的封装让您几乎感受不到网络的存在；<span style={{"fontWeight":"bold",fontSize:"13pt"}}>有趣：</span>丰富直白的API让开发人员轻松操控一切</div>
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
            <div className={styles.itemTitle}>极震撼的性能</div>
            <div className={styles.itemInfo}>
              <p>单机支持百万级tcp长连接（<a href="https://my.oschina.net/u/2369298/blog/915435" target="_blank">附报告</a>）</p>
              <p>有网友曾测出每秒收发500万条聊天消息，约165M（<a href="https://gitee.com/tywo45/t-io/raw/master/docs/performance/500%E4%B8%87.png" target="_blank">附 图</a>）</p>
            </div>
          </div>
          <div className={styles.traitInfoItem}>
            <div className={styles.itemTitle}>丰富的API，开发业务之利器</div>
            <div className={styles.itemInfo}>
              <p>内置心跳检测、心跳发送</p>
              <p>内置各种形态的消息推送API</p>
              <p>内置最强最细级别的数据监控API</p>
              <p>内置断链自动重连</p>
            </div>
          </div>

          <div className={styles.traitInfoItem}>
            <div className={styles.itemTitle}>丰富的案例和正在完善的生态</div>
            <div className={styles.itemInfo}>
              <p>tio-http-server已经发布</p>
              <p>tio-websocket-server已经发布</p>
              <p>tio-webpack已经发布</p>
              <p>tio-im已经开源（正在完善，暂时仅供参考）</p>
              <p>已经被记录在册的案例（<a href="http://www.t-io.org/#/case" target="_blank">查 看</a>）</p>
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
