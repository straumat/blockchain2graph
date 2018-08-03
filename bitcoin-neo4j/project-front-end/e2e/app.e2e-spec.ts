import {AppPage} from './app.po';
import {Blockchain2graphService} from '../src/app/blockchain2graph.service';

describe('project-front-end App', () => {
  let page: AppPage;
  let blockchain2graphService;

  beforeEach(() => {
    blockchain2graphService = new Blockchain2graphService();
    page = new AppPage();
    // Blockchain2graph message simulation.
    /*    mockServer.on('connection', () => {
          //mockServer.send('test message 1');
          //mockServer.send('test message 2');
          let sammy = {
            "messageType": "blocksInBitcoinCore",
            "messageValue": 1
          };
          mockServer.send(sammy);
        });*/
  });

  it('should display Blockchain2graph', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('Blockchain2graph');

    /*const socket = new WebSocket('ws://localhost:8080');
    socket.onmessage = (event) => {
      console.log(' toto ' + event.data);
    };*/

  });

});
