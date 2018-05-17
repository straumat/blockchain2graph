import {Injectable, OnDestroy} from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class Blockchain2graphService implements OnDestroy {

  // Websocket connexion.
  private static serverUrl = 'ws://localhost:8080/status/websocket';
  private webSocket: WebSocket;

  // Status values.
  private readonly blocksCountInBitcoinCoreSubject: BehaviorSubject<Object> = new BehaviorSubject<Object>(-1);
  private readonly blocksCountInNeo4jSubject: BehaviorSubject<Object> = new BehaviorSubject<Object>(-1);
  private readonly averageBlockProcessDurationSubject: BehaviorSubject<Object> = new BehaviorSubject<Object>(-1);

  // Observable.
  public readonly blocksCountInBitcoinCore: Observable<Object> = this.blocksCountInBitcoinCoreSubject.asObservable();
  public readonly blocksCountInNeo4j: Observable<Object> = this.blocksCountInNeo4jSubject.asObservable();
  public readonly averageBlockProcessDuration: Observable<Object> = this.averageBlockProcessDurationSubject.asObservable();

  constructor() {
    this.webSocket = new WebSocket(Blockchain2graphService.serverUrl);
    this.webSocket.addEventListener('message', message => {
      this.processMessage(JSON.parse(message.data));
    });
  }

  /**
   * Triggered when a new message is coming from the server.
   * @param message blockchain2graph server message.
   */
  public processMessage(message) {
    const status = JSON.parse(message);

    // blocksCountInBitcoinCoreSubject.
    if (status.blocksCountInBitcoinCore !== this.blocksCountInBitcoinCoreSubject.getValue()) {
      this.blocksCountInBitcoinCoreSubject.next(status.blocksCountInBitcoinCore);
    }

    // blocksCountInNeo4j.
    if (status.blocksCountInNeo4j !== this.blocksCountInNeo4jSubject.getValue()) {
      this.blocksCountInNeo4jSubject.next(status.blocksCountInNeo4j);
    }

    // averageBlockProcessDuration.
    if (status.averageBlockProcessDuration !== this.averageBlockProcessDurationSubject.getValue()) {
      this.averageBlockProcessDurationSubject.next(status.averageBlockProcessDuration);
    }
  }

  ngOnDestroy(): void {
    this.webSocket.close();
  }

}
