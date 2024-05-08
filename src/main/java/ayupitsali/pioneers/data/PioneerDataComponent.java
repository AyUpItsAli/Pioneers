package ayupitsali.pioneers.data;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface PioneerDataComponent extends Component {
    int getLives();
    void setLives(int lives);
}
